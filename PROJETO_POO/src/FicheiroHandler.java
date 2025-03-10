import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por carregar e salvar dados de clientes e faturas em ficheiros de texto.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */

public class FicheiroHandler {
    private static final String FICHEIRO_CLIENTES = "clientes.txt";

    /**
     * Carrega os clientes e as faturas a partir de um ficheiro de texto.
     *
     * @param faturas Lista de faturas que será preenchida com os dados do ficheiro.
     * @return Lista de clientes carregados do ficheiro.
     */

    public List<Cliente> carregarClientesComFaturas(List<Fatura> faturas) {
        List<Cliente> clientes = new ArrayList<>();
        File ficheiro = new File(FICHEIRO_CLIENTES);

        if (!ficheiro.exists()) {
            System.out.println("Ficheiro não encontrado. Criando um novo.");
            return clientes;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            boolean lendoClientes = true;

            Cliente clienteAtual = null;
            Fatura faturaAtual = null;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                // Ignorar comentários e linhas vazias
                if (linha.startsWith("#") || linha.isEmpty()) {
                    if (linha.startsWith("# Faturas")) {
                        lendoClientes = false;
                    }
                    continue;
                }

                if (lendoClientes) {
                    // Processar clientes
                    String[] partes = linha.split(";");
                    if (partes.length == 3) {
                        String nome = partes[0].trim();
                        String tipoLocalizacao = partes[1].trim();
                        int numeroContribuinte = Integer.parseInt(partes[2].trim());

                        Cliente cliente = new Cliente(nome, tipoLocalizacao, numeroContribuinte);
                        clientes.add(cliente);
                    } else {
                        System.out.println("Linha inválida para cliente: " + linha);
                    }
                } else {
                    // Processar faturas e produtos
                    String[] partes = linha.split(";");
                    if (partes.length == 3) {
                        // Ler dados da fatura
                        int numeroFatura = Integer.parseInt(partes[0].trim());
                        LocalDate data = LocalDate.parse(partes[1].trim());
                        int numeroContribuinte = Integer.parseInt(partes[2].trim());

                        // Buscar cliente correspondente
                        Cliente cliente = clientes.stream()
                                .filter(c -> c.getNumeroContribuinte() == numeroContribuinte)
                                .findFirst()
                                .orElse(null);

                        if (cliente == null) {
                            System.out.println("Cliente com número de contribuinte " + numeroContribuinte + " não encontrado. Fatura ignorada.");
                            continue;
                        }

                        Fatura fatura = new Fatura(numeroFatura, cliente, data);
                        faturas.add(fatura);
                        clienteAtual = cliente;
                        faturaAtual = fatura;
                    } else if (partes.length >= 6 && clienteAtual != null && faturaAtual != null) {
                        // Ler produtos da fatura
                        String codigo = partes[0].trim();
                        String nome = partes[1].trim();
                        String descricao = partes[2].trim();
                        String tipo = partes[3].trim();
                        double valorUnitario = Double.parseDouble(partes[4].trim());
                        int quantidade = Integer.parseInt(partes[5].trim());
                        String categoria = partes[6].equalsIgnoreCase("null") ? null : partes[6].trim();

                        Produto produto = criarProduto(tipo, codigo, nome, descricao, valorUnitario, quantidade, categoria, partes);

                        if (produto != null) {
                            faturaAtual.adicionarProduto(produto);
                        }
                    } else {
                        System.out.println("Linha inválida para produto: " + linha);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar dados do ficheiro: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }

        return clientes;
    }
    /**
     * Cria um produto baseado nos dados fornecidos.
     *
     * @param tipo Tipo do produto ("alimentar" ou "farmácia").
     * @param codigo Código do produto.
     * @param nome Nome do produto.
     * @param descricao Descrição do produto.
     * @param valorUnitario Valor unitário do produto.
     * @param quantidade Quantidade do produto.
     * @param categoria Categoria do produto (ou null).
     * @param partes Dados adicionais sobre o produto.
     * @return O produto criado ou null se os dados forem inválidos.
     */

    private Produto criarProduto(String tipo, String codigo, String nome, String descricao, double valorUnitario, int quantidade, String categoria, String[] partes) {
        // Verificar se tipo é nulo e tratar isso
        if (tipo == null || tipo.trim().isEmpty()) {
            System.out.println("Tipo do produto não pode ser nulo ou vazio. Produto ignorado.");
            return null;
        }

        // Normaliza o tipo do produto para comparação consistente
        tipo = tipo.trim().toLowerCase();

        if (tipo.equals("alimentar")) {
            boolean biologico = partes.length > 7 && partes[7].equalsIgnoreCase("true");
            List<String> certificacoes = new ArrayList<>();

            if (partes.length > 8 && partes[8] != null && !partes[8].isEmpty()) {
                certificacoes = List.of(partes[8].split(","));
            }

            String tipoTaxa = "Taxa reduzida";


            if (certificacoes.isEmpty() && !biologico) {
                tipoTaxa = "Taxa normal";
            } else if (!certificacoes.isEmpty() && categoria != null && categoria.equalsIgnoreCase("congelados")) {
                tipoTaxa = "Taxa intermédia";
            }

            // Se a taxa for "Taxa reduzida" ou "Taxa normal", categoria deve ser null
            if (tipoTaxa.equals("Taxa reduzida") || tipoTaxa.equals("Taxa normal")) {
                categoria = null;
            }

            // Validação específica para produtos com "Taxa Reduzida"
            if (tipoTaxa.equals("Taxa reduzida") && (certificacoes.size() < 1 || certificacoes.size() > 4)) {
                System.out.println("Erro ao criar Produto Alimentar: Produtos com Taxa Reduzida devem ter entre 1 e 4 certificações.");
                return null;
            }

            return new ProdutoAlimentar(codigo, nome, descricao, valorUnitario, tipoTaxa, biologico, certificacoes, categoria, quantidade);
        } else if (tipo.equals("farmácia") || tipo.equals("farmacia")) {
            boolean comPrescricao = partes.length > 7 && partes[7] != null && partes[7].equalsIgnoreCase("true");
            String medico = partes.length > 8 && partes[8] != null && !partes[8].equalsIgnoreCase("null") ? partes[8].trim() : null;

            // Se for um produto de farmácia sem prescrição, pode ter uma categoria
            if (!comPrescricao) {
                if (categoria != null && !categoria.trim().isEmpty()) {
                    if (!categoria.equalsIgnoreCase("Beleza") &&
                            !categoria.equalsIgnoreCase("Bem-estar") &&
                            !categoria.equalsIgnoreCase("Bebês") &&
                            !categoria.equalsIgnoreCase("Animais") &&
                            !categoria.equalsIgnoreCase("Outro")) {
                        System.out.println("Categoria inválida para produto de farmácia sem prescrição: " + categoria);
                        return null;
                    }
                }
            } else {

                categoria = null;
            }

            return new ProdutoFarmacia(codigo, nome, descricao, valorUnitario, comPrescricao, categoria, medico, quantidade, "Taxa normal");
        }

        System.out.println("Tipo de produto inválido: " + tipo + ". Produto ignorado.");
        return null;
    }







    public void salvarClientesComFaturas(List<Cliente> clientes, List<Fatura> faturas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FICHEIRO_CLIENTES))) {
            // Salvar clientes
            bw.write("# Clientes\n");
            for (Cliente cliente : clientes) {
                bw.write(cliente.getNome() + ";" + cliente.getTipoLocalizacao() + ";" + cliente.getNumeroContribuinte());
                bw.newLine();
            }

            // Salvar faturas
            bw.write("# Faturas\n");
            for (Fatura fatura : faturas) {
                bw.write(fatura.getNumero() + ";" + fatura.getData() + ";" + fatura.getCliente().getNumeroContribuinte());
                bw.newLine();

                // Salvar produtos da fatura
                for (Produto produto : fatura.getProdutos()) {
                    String produtoDetalhes = produto.getCodigo() + ";" + produto.getNome() + ";" + produto.getDescricao() + ";" +
                            produto.getTipo() + ";" + produto.getValorUnitario() + ";" + produto.getQuantidade() + ";" +
                            (produto.getCategoria() != null ? produto.getCategoria() : "null");

                    if (produto.getTipo().equalsIgnoreCase("Alimentar")) {
                        ProdutoAlimentar alimentar = (ProdutoAlimentar) produto;
                        produtoDetalhes += ";" + (alimentar.isBiologico() ? "true" : "false") + ";";
                        produtoDetalhes += String.join(",", alimentar.getCertificacoes());
                    } else if (produto.getTipo().equalsIgnoreCase("Farmácia")) {
                        ProdutoFarmacia farmacia = (ProdutoFarmacia) produto;
                        produtoDetalhes += ";" + (farmacia.isComPrescricao() ? "true" : "false") + ";";
                        produtoDetalhes += (farmacia.getMedico() != null ? farmacia.getMedico() : "null");
                    }
                    bw.write(produtoDetalhes);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar clientes e faturas: " + e.getMessage());
        }
    }
}
