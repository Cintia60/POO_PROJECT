import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.time.format.DateTimeParseException;

/**
 * Classe que representa o Sistema de Gestão da aplicação.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */



public class POOFS {
    // Atributos principais do sistema

    /**
     * Lista de clientes registrados.
     */
    private List<Cliente> clientes;
    /**
     * Lista de faturas registradas.
     */
    private List<Fatura> faturas;
    /**
     * Handler para manipulação de ficheiros de texto.
     */
    private FicheiroHandler ficheiroHandler;

    /**
     * Handler para manipulação de ficheiros de objetos serializados.
     */
    private FicheiroObjetoHandler ficheiroObjetoHandler;
    /**
     * Scanner para captura de entrada do utilizador.
     * Transiente para não ser serializado.
     */
    private transient Scanner scanner;

    /**
     * Construtor da classe POOFS.
     * Inicializa os handlers e carrega os dados de clientes e faturas.
     */


    public POOFS() {
        this.ficheiroHandler = new FicheiroHandler();
        this.ficheiroObjetoHandler = new FicheiroObjetoHandler();
        this.faturas = new ArrayList<>();

        // Carregar dados de ficheiro de objetos
        ficheiroObjetoHandler.carregarDados();
        this.clientes = ficheiroObjetoHandler.getClientes();
        this.faturas = ficheiroObjetoHandler.getFaturas();


        if (this.clientes.isEmpty()) {
            this.clientes = ficheiroHandler.carregarClientesComFaturas(faturas);
        }
        this.scanner = new Scanner(System.in);
    }

    /**
     * Cria um novo cliente no sistema.
     * Solicita informações do utilizador e valida os dados.
     */

    public void criarCliente() {
        String nome = null;
        boolean nomeValido = false;

        while (!nomeValido) {
            System.out.println("Nome do cliente:");
            nome = scanner.nextLine().trim();

            // Verificar se o nome não está vazio e não é numérico
            if (nome.isEmpty() || nome.matches("\\d+")) {
                System.out.println("Nome inválido! O nome não pode ser vazio ou numérico.");
                System.out.print("Deseja tentar novamente? (s/n): ");
                String resposta = scanner.nextLine().trim().toLowerCase();
                if (!resposta.equals("s")) {
                    System.out.println("Operação cancelada.");
                    return;
                }
            } else {
                nomeValido = true;
            }
        }

        System.out.println("Localização do cliente (Continente, Madeira ou Açores):");
        String tipoLocalizacao = scanner.nextLine().trim();
        while (!tipoLocalizacao.equalsIgnoreCase("Continente") &&
                !tipoLocalizacao.equalsIgnoreCase("Madeira") &&
                !tipoLocalizacao.equalsIgnoreCase("Açores")) {
            System.out.println("Localização inválida. Por favor, insira: Continente, Madeira ou Açores:");
            tipoLocalizacao = scanner.nextLine().trim();
        }

        // Usar InputUtils para validar e ler o número de contribuinte
        String numeroContribuinte = InputUtils.lerNifValido("Número de contribuinte do cliente (4 dígitos): ");

        // Verificar se o NIF já existe
        if (clientes.stream().anyMatch(c -> String.valueOf(c.getNumeroContribuinte()).equals(numeroContribuinte))) {
            System.out.println("Já existe um cliente com este número de contribuinte.");
            return;
        }

        // Criar cliente e adicionar à lista
        Cliente cliente = new Cliente(nome, tipoLocalizacao, Integer.parseInt(numeroContribuinte));
        clientes.add(cliente);
        ficheiroHandler.salvarClientesComFaturas(clientes, faturas);
        System.out.println("Cliente criado com sucesso!");
    }
    /**
     * Edita os dados de um cliente existente.
     * Permite modificar nome, localização e número de contribuinte.
     */
    public void editarCliente() {
        System.out.println("Informe o número de contribuinte do cliente a ser editado:");
        int numeroContribuinte = scanner.nextInt();
        scanner.nextLine();

        Cliente cliente = clientes.stream()
                .filter(c -> c.getNumeroContribuinte() == numeroContribuinte)
                .findFirst()
                .orElse(null);

        if (cliente == null) {
            System.out.println("Cliente não encontrado.");
            return;
        }

        System.out.println("Editando cliente: " + cliente);

        System.out.println("Informe o novo nome do cliente (Enter para manter o atual):");
        String novoNome = scanner.nextLine();
        if (!novoNome.trim().isEmpty()) {
            cliente.setNome(novoNome);
        }

        System.out.println("Nova localização (Continente, Madeira ou Açores) (Enter para manter a atual):");
        String novaLocalizacao = scanner.nextLine();
        if (!novaLocalizacao.trim().isEmpty() &&
                (novaLocalizacao.equalsIgnoreCase("Continente") ||
                        novaLocalizacao.equalsIgnoreCase("Madeira") ||
                        novaLocalizacao.equalsIgnoreCase("Açores"))) {
            cliente.setTipoLocalizacao(novaLocalizacao);
        }

        System.out.println("Novo número de contribuinte (ou 0 para manter o atual):");
        int novoNumeroContribuinte = scanner.nextInt();
        scanner.nextLine();
        if (novoNumeroContribuinte > 0) {
            cliente.setNumeroContribuinte(novoNumeroContribuinte);
        }

        ficheiroHandler.salvarClientesComFaturas(clientes, faturas);
        System.out.println("Cliente editado com sucesso: " + cliente);
    }

    /**
     * Lista todos os clientes cadastrados no sistema.
     * Exibe os dados no console.
     */

    public void listarClientes() {
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            for (Cliente cliente : clientes) {
                System.out.println(cliente);
            }
        }
    }

    /**
     * Cria uma nova fatura associada a um cliente.
     * Solicita informações do utilizador e adiciona produtos à fatura.
     */

    public void criarFatura() {
        Scanner scanner = new Scanner(System.in);
        String numeroContribuinte = InputUtils.lerNifValido("Informe o número de contribuinte do cliente (4 dígitos): ");
        scanner.nextLine();

        // Buscar o cliente correspondente
        Cliente cliente = clientes.stream()
                .filter(c -> String.valueOf(c.getNumeroContribuinte()).equals(numeroContribuinte))
                .findFirst()
                .orElse(null);

        if (cliente == null) {
            System.out.println("Cliente não encontrado.");
            return;
        }

        LocalDate dataFatura = InputUtils.lerData("Informe a data da fatura (yyyy-MM-dd): ");


        Fatura fatura = new Fatura(faturas.size() + 1, cliente, dataFatura);

        System.out.println("Informe o número de produtos a adicionar:");
        int numProdutos = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numProdutos; i++) {
            System.out.println("Produto adicionado nr: " + (i + 1) + ":");
            System.out.println("O produto é de qual tipo? (1 - Alimentar, 2 - Farmácia):");
            int tipoProduto = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Informe o código do produto:");
            String codigo = scanner.nextLine();

            System.out.println("Informe o nome do produto:");
            String nome = scanner.nextLine();

            System.out.println("Informe a descrição do produto:");
            String descricao = scanner.nextLine();

            double valorUnitario = InputUtils.lerDouble("Informe o valor unitário (sem IVA): ");

            System.out.println("Informe a quantidade:");
            int quantidade = scanner.nextInt();
            scanner.nextLine();

            Produto produto;

            if (tipoProduto == 1) {
                boolean biologico = false;
                List<String> certificacoes = new ArrayList<>();
                String categoria = null;

                System.out.println("Informe o tipo de taxa (Taxa reduzida, Taxa intermédia, Taxa normal):");
                String tipoTaxa = scanner.nextLine();

                if (tipoTaxa.equalsIgnoreCase("Taxa reduzida")) {
                    System.out.println("O produto é biológico? (true/false):");
                    biologico = scanner.nextBoolean();
                    scanner.nextLine();

                    System.out.println("Informe o número de certificações (1 a 4):");
                    int numCertificacoes = scanner.nextInt();
                    scanner.nextLine();

                    if (numCertificacoes < 1 || numCertificacoes > 4) {
                        System.out.println("Número de certificações inválido. Deve estar entre 1 e 4.");
                        return;
                    }

                    System.out.println("Escolha as certificações (1 - ISO22000, 2 - FSSC22000, 3 - HACCP, 4 - GMP):");
                    for (int j = 0; j < numCertificacoes; j++) {
                        System.out.print("Certificação " + (j + 1) + ": ");
                        int escolha = scanner.nextInt();
                        scanner.nextLine();

                        switch (escolha) {
                            case 1 -> certificacoes.add("ISO22000");
                            case 2 -> certificacoes.add("FSSC22000");
                            case 3 -> certificacoes.add("HACCP");
                            case 4 -> certificacoes.add("GMP");
                            default -> {
                                System.out.println("Escolha inválida.");
                                return;
                            }
                        }
                    }
                } else if (tipoTaxa.equalsIgnoreCase("Taxa intermédia")) {
                    System.out.println("Informe a categoria (congelados, enlatados, vinho):");
                    categoria = scanner.nextLine();

                    if (!categoria.equalsIgnoreCase("congelados") &&
                            !categoria.equalsIgnoreCase("enlatados") &&
                            !categoria.equalsIgnoreCase("vinho")) {
                        System.out.println("Categoria inválida para produtos com Taxa Intermédia.");
                        return;
                    }
                } else if (tipoTaxa.equalsIgnoreCase("Taxa normal")) {
                    categoria = null;
                } else {
                    System.out.println("Tipo de taxa inválido.");
                    return;
                }

                produto = new ProdutoAlimentar(codigo, nome, descricao, valorUnitario,
                        tipoTaxa, biologico, certificacoes, categoria, quantidade);

            } else if (tipoProduto == 2) {
                System.out.println("O produto requer prescrição? (true/false):");
                boolean comPrescricao = scanner.nextBoolean();
                scanner.nextLine();

                String categoria = null;
                String medico = null;

                if (comPrescricao) {
                    System.out.println("Informe o nome do médico que prescreveu:");
                    medico = scanner.nextLine();
                } else {
                    System.out.println("Informe a categoria (Beleza, Bem-estar, Bebês, Animais, Outro):");
                    categoria = scanner.nextLine();

                    if (!categoria.equalsIgnoreCase("Beleza") &&
                            !categoria.equalsIgnoreCase("Bem-estar") &&
                            !categoria.equalsIgnoreCase("Bebês") &&
                            !categoria.equalsIgnoreCase("Animais") &&
                            !categoria.equalsIgnoreCase("Outro")) {
                        System.out.println("Categoria inválida para produtos de Farmácia.");
                        return;
                    }
                }

                produto = new ProdutoFarmacia(codigo, nome, descricao, valorUnitario,
                        comPrescricao, categoria, medico, quantidade, null);
            } else {
                System.out.println("Tipo de produto inválido. Pulando este produto.");
                continue;
            }

            fatura.adicionarProduto(produto);
            System.out.println("Produto adicionado com sucesso!");
        }

        faturas.add(fatura);
        ficheiroHandler.salvarClientesComFaturas(clientes, faturas);
        System.out.println("Fatura criada com sucesso!");
    }

    /**
     * Edita uma fatura existente no sistema.
     * Permite modificar a data e os produtos da fatura.
     */

    public void editarFatura() {
        Scanner scanner = new Scanner(System.in);
        Fatura fatura = null;

        // Loop para permitir tentar novamente se a fatura não for encontrada
        while (fatura == null) {
            System.out.println("Informe o número da fatura a ser editada:");
            int numeroFatura = InputUtils.lerInt("Número da fatura: ");

            fatura = faturas.stream()
                    .filter(f -> f.getNumero() == numeroFatura)
                    .findFirst()
                    .orElse(null);

            if (fatura == null) {
                System.out.println("Fatura não encontrada.");
                System.out.print("Deseja tentar novamente? (s/n): ");
                String resposta = scanner.nextLine().trim().toLowerCase();
                if (!resposta.equals("s")) {
                    System.out.println("Operação cancelada.");
                    return;
                }
            }
        }

        System.out.println("Editando fatura: " + fatura.getNumero());

        // Permitir que o utilizador insira uma nova data ou mantenha a atual
        System.out.println("Informe a nova data da fatura (yyyy-MM-dd) (ou pressione Enter para manter a atual):");
        String novaData = scanner.nextLine().trim();
        if (!novaData.isEmpty()) {
            try {
                fatura.setData(LocalDate.parse(novaData));
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida! Mantendo a data atual.");
            }
        }

        // Editar produtos
        List<Produto> novosProdutos = new ArrayList<>();
        System.out.println("Informe o número de produtos a adicionar/editados (ou pressione Enter para manter os atuais):");
        String entradaProdutos = scanner.nextLine().trim();
        if (!entradaProdutos.isEmpty()) {
            int numProdutos;
            try {
                numProdutos = Integer.parseInt(entradaProdutos);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Mantendo os produtos atuais.");
                return;
            }

            for (int i = 0; i < numProdutos; i++) {
                System.out.println("Produto a ser editado/adicionado nr: " + (i + 1));
                System.out.println("O produto é de qual tipo? (1 - Alimentar, 2 - Farmácia):");
                int tipoProduto = InputUtils.lerInt("Tipo de produto: ");

                System.out.print("Informe o código do produto (ou pressione Enter para pular): ");
                String codigo = scanner.nextLine().trim();
                if (codigo.isEmpty()) continue;

                System.out.print("Informe o nome do produto (ou pressione Enter para pular): ");
                String nome = scanner.nextLine().trim();
                if (nome.isEmpty()) continue;

                System.out.print("Informe a descrição do produto (ou pressione Enter para pular): ");
                String descricao = scanner.nextLine().trim();
                if (descricao.isEmpty()) continue;

                double valorUnitario = InputUtils.lerDouble("Informe o valor unitário (sem IVA): ");
                int quantidade = InputUtils.lerInt("Informe a quantidade: ");

                Produto produto;

                if (tipoProduto == 1) { // Produto Alimentar
                    boolean biologico = false;
                    List<String> certificacoes = new ArrayList<>();
                    String categoria = null;

                    System.out.print("Informe o tipo de taxa (Taxa reduzida, Taxa intermédia, Taxa normal): ");
                    String tipoTaxa = scanner.nextLine();

                    if (tipoTaxa.equalsIgnoreCase("Taxa reduzida")) {
                        System.out.println("O produto é biológico? (true/false):");
                        biologico = scanner.nextBoolean();
                        scanner.nextLine();

                        System.out.println("Informe o número de certificações (1 a 4):");
                        int numCertificacoes = InputUtils.lerInt("Número de certificações: ");

                        if (numCertificacoes < 1 || numCertificacoes > 4) {
                            System.out.println("Número de certificações inválido. Deve estar entre 1 e 4.");
                            return;
                        }

                        System.out.println("Escolha as certificações (1 - ISO22000, 2 - FSSC22000, 3 - HACCP, 4 - GMP):");
                        for (int j = 0; j < numCertificacoes; j++) {
                            System.out.print("Certificação " + (j + 1) + ": ");
                            int escolha = InputUtils.lerInt("Escolha: ");

                            switch (escolha) {
                                case 1 -> certificacoes.add("ISO22000");
                                case 2 -> certificacoes.add("FSSC22000");
                                case 3 -> certificacoes.add("HACCP");
                                case 4 -> certificacoes.add("GMP");
                                default -> System.out.println("Escolha inválida.");
                            }
                        }
                    } else if (tipoTaxa.equalsIgnoreCase("Taxa intermédia")) {
                        System.out.println("Informe a categoria (congelados, enlatados, vinho):");
                        categoria = scanner.nextLine();

                        if (!categoria.equalsIgnoreCase("congelados") &&
                                !categoria.equalsIgnoreCase("enlatados") &&
                                !categoria.equalsIgnoreCase("vinho")) {
                            System.out.println("Categoria inválida para produtos com Taxa Intermédia.");
                            return;
                        }
                    } else if (!tipoTaxa.equalsIgnoreCase("Taxa normal")) {
                        System.out.println("Tipo de taxa inválido.");
                        return;
                    }

                    produto = new ProdutoAlimentar(codigo, nome, descricao, valorUnitario,
                            tipoTaxa, biologico, certificacoes, categoria, quantidade);

                } else if (tipoProduto == 2) { // Produto de Farmácia
                    System.out.print("O produto requer prescrição? (true/false): ");
                    boolean comPrescricao = scanner.nextBoolean();
                    scanner.nextLine();

                    String categoria = null;
                    String medico = null;

                    if (comPrescricao) {
                        System.out.print("Informe o nome do médico que prescreveu (ou pressione Enter para nenhum): ");
                        medico = scanner.nextLine();
                    } else {
                        System.out.print("Informe a categoria (Beleza, Bem-estar, Bebês, Animais, Outro): ");
                        categoria = scanner.nextLine();

                        if (!categoria.equalsIgnoreCase("Beleza") &&
                                !categoria.equalsIgnoreCase("Bem-estar") &&
                                !categoria.equalsIgnoreCase("Bebês") &&
                                !categoria.equalsIgnoreCase("Animais") &&
                                !categoria.equalsIgnoreCase("Outro")) {
                            System.out.println("Categoria inválida para produtos de Farmácia.");
                            return;
                        }
                    }

                    produto = new ProdutoFarmacia(codigo, nome, descricao, valorUnitario,
                            comPrescricao, categoria, medico, quantidade, "Taxa normal");
                } else {
                    System.out.println("Tipo de produto inválido. Pulando este produto.");
                    continue;
                }

                novosProdutos.add(produto);
            }

            fatura.setProdutos(novosProdutos);
        }

        ficheiroHandler.salvarClientesComFaturas(clientes, faturas);
        System.out.println("Fatura editada com sucesso.");
    }


    /**
     * Lista todas as faturas registradas no sistema.
     * Exibe os detalhes principais de cada fatura.
     */

    public void listarFaturas() {
        if (faturas.isEmpty()) {
            System.out.println("Nenhuma fatura registrada.");
        } else {
            for (Fatura fatura : faturas) {
                System.out.println("Fatura Nº: " + fatura.getNumero());
                System.out.println("Cliente: " + fatura.getCliente().getNome());
                System.out.println("Localização: " + fatura.getCliente().getTipoLocalizacao());
                System.out.println("Número de Produtos: " + fatura.getProdutos().size());
                System.out.println("Valor Total Sem IVA: " + fatura.getValorTotalSemIVA());
                System.out.println("Valor Total Com IVA: " + fatura.getValorTotalComIVA());
                System.out.println("--------------------------------------------------");
            }
        }

    }

    /**
     * Exibe os detalhes de uma fatura específica.
     * Inclui informações do cliente, produtos e totais.
     */

    public void visualizarFatura() {
        System.out.println("Número da fatura a ser visualizada:");
        int numeroFatura = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        // Buscar a fatura pelo número
        Fatura fatura = faturas.stream()
                .filter(f -> f.getNumero() == numeroFatura)
                .findFirst()
                .orElse(null);

        if (fatura == null) {
            System.out.println("Fatura não encontrada.");
            return;
        }

        // Exibir dados da fatura
        System.out.println("Fatura Nº: " + fatura.getNumero());
        System.out.println("Data: " + fatura.getData());
        System.out.println("Cliente: " + fatura.getCliente().getNome());
        System.out.println("Localização: " + fatura.getCliente().getTipoLocalizacao());
        System.out.println("Número de Contribuinte: " + fatura.getCliente().getNumeroContribuinte());
        System.out.println("--------------------------------------------------");

        // Exibir os produtos da fatura
        System.out.println("Produtos:");
        System.out.printf("%-10s %-20s %-20s %-15s %-10s %-15s %-12s %-10s %-10s %-10s %-10s %-10s %-10s\n",
                "Código", "Nome", "Descrição", "Tipo", "Qtd", "Categoria", "Valor Unit.", "Taxa IVA", "Valor IVA",
                "Total c/ IVA", "Biológico", "Prescrição", "Certif.");

        for (Produto produto : fatura.getProdutos()) {
            // Detalhes específicos por tipo de produto
            String detalhesEspecificos = produto.getDetalhesFatura(fatura.getCliente().getTipoLocalizacao());

            // Calculando os valores para IVA
            double valorSemIVA = produto.getValorUnitario() * produto.getQuantidade();
            double taxaIVA = produto.calcularImposto(fatura.getCliente().getTipoLocalizacao()) / valorSemIVA * 100;
            double valorIVA = produto.calcularImposto(fatura.getCliente().getTipoLocalizacao());
            double valorTotalComIVA = valorSemIVA + valorIVA;

            // Exibir os detalhes
            System.out.printf("%-10s %-20s %-20s %-15s %-10d %-15s %-12.2f %-10.2f %-10.2f %-10.2f %-10s\n",
                    produto.getCodigo(), produto.getNome(), produto.getDescricao(), produto.getTipo(),
                    produto.getQuantidade(), produto.getCategoria(), produto.getValorUnitario(), taxaIVA, valorIVA,
                    valorTotalComIVA, detalhesEspecificos);
        }

        System.out.println("--------------------------------------------------");

        // Exibir totais da fatura
        System.out.printf("Total Sem IVA: %.2f\n", fatura.getValorTotalSemIVA());
        System.out.printf("Total do IVA: %.2f\n", fatura.getValorTotalIva());
        System.out.printf("Total Com IVA: %.2f\n", fatura.getValorTotalComIVA());
    }

    /**
     * Importa faturas e clientes de um ficheiro de texto.
     * Atualiza as listas de clientes e faturas no sistema.
     */

    public void importarFaturas() {
        List<Fatura> faturasImportadas = new ArrayList<>();
        List<Cliente> clientesImportados = ficheiroHandler.carregarClientesComFaturas(faturasImportadas);

        int faturasImportadasComSucesso = faturasImportadas.size();

        if (faturasImportadasComSucesso > 0) {
            // Atualizar listas de clientes e faturas na memória
            clientes.clear();
            clientes.addAll(clientesImportados);

            faturas.clear();
            faturas.addAll(faturasImportadas);
        }

        System.out.println(faturasImportadasComSucesso + " faturas foram importadas com sucesso.");
    }

    /**
     * Exporta as faturas registradas para um ficheiro de texto.
     * Solicita o caminho do ficheiro ao utilizador.
     */

    public void exportarFaturas() {
        System.out.println("Informe o caminho do ficheiro para exportar as faturas (ex: faturas.txt):");
        String caminhoFicheiro = scanner.nextLine();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoFicheiro))) {
            for (Fatura fatura : faturas) {
                bw.write("Fatura Nº: " + fatura.getNumero() + "\n");
                bw.write("Cliente: " + fatura.getCliente().getNome() + "\n");
                bw.write("Data: " + fatura.getData() + "\n");
                bw.write("Número de Contribuinte: " + fatura.getCliente().getNumeroContribuinte() + "\n");
                bw.write("Produtos:\n");
                for (Produto produto : fatura.getProdutos()) {
                    bw.write("  Código: " + produto.getCodigo() + ", Nome: " + produto.getNome() + ", Descrição: " + produto.getDescricao() + ", Quantidade: " + produto.getQuantidade() + ", Valor Unitário: " + produto.getValorUnitario() + "\n");
                }
                bw.write("Valor Total Sem IVA: " + fatura.getValorTotalSemIVA() + "\n");
                bw.write("Valor Total Com IVA: " + fatura.getValorTotalComIVA() + "\n");
                bw.write("--------------------------------------------------\n");
            }
            System.out.println("Faturas exportadas com sucesso para " + caminhoFicheiro);
        } catch (IOException e) {
            System.out.println("Erro ao exportar faturas: " + e.getMessage());
        }
    }

    /**
     * Exibe estatísticas gerais do sistema.
     * Inclui número de faturas, produtos e valores totais.
     */

    public void exibirEstatisticas() {
        int numeroDeFaturas = faturas.size(); // Número de faturas
        int numeroDeProdutos = faturas.stream()
                .flatMap(f -> f.getProdutos().stream())
                .mapToInt(Produto::getQuantidade) // Soma todas as quantidades
                .sum();
        double totalSemIVA = faturas.stream()
                .mapToDouble(Fatura::getValorTotalSemIVA) // Soma os valores sem IVA de todas as faturas
                .sum();
        double totalIVA = faturas.stream()
                .mapToDouble(Fatura::getValorTotalIva) // Soma os valores do IVA de todas as faturas
                .sum();
        double totalComIVA = faturas.stream()
                .mapToDouble(Fatura::getValorTotalComIVA) // Soma os valores com IVA de todas as faturas
                .sum();

        System.out.println("\nEstatísticas:");
        System.out.println("Número de faturas: " + numeroDeFaturas);
        System.out.println("Número de produtos: " + numeroDeProdutos);
        System.out.printf("Valor total sem IVA: %.2f\n", totalSemIVA);
        System.out.printf("Valor total do IVA: %.2f\n", totalIVA);
        System.out.printf("Valor total com IVA: %.2f\n", totalComIVA);
    }

    /**
     * Metodo personalizado para serialização.
     * Recria o Scanner após a deserialização.
     *
     * @param ois ObjectInputStream para leitura de objetos serializados.
     * @throws IOException            Erro de entrada/saída.
     * @throws ClassNotFoundException Classe não encontrada durante a deserialização.
     */

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.scanner = new Scanner(System.in);
    }
    /**
     * Obtém o handler responsável pela manipulação de ficheiros de objetos.
     *
     * @return Instância de FicheiroObjetoHandler.
     */

    public FicheiroObjetoHandler getFicheiroObjetoHandler() {
        return ficheiroObjetoHandler;
    }



}
