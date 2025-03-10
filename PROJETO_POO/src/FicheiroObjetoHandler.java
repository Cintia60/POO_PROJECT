import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por salvar e carregar objetos de clientes e faturas
 * em um ficheiro binário usando serialização.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */
class FicheiroObjetoHandler {
    private static final String FICHEIRO_OBJETOS = "dados.obj";

    // Listas para armazenar os dados de clientes e faturas
    private List<Cliente> clientes;
    private List<Fatura> faturas;

    /**
     * Construtor da classe. Inicializa as listas de clientes e faturas e tenta carregar dados do ficheiro.
     */
    public FicheiroObjetoHandler() {
        this.clientes = new ArrayList<>();
        this.faturas = new ArrayList<>();
        carregarDados();
    }

    // Getters para acessar as listas de clientes e faturas

    /**
     * Retorna a lista de clientes.
     *
     * @return Lista de clientes.
     */
    public List<Cliente> getClientes() {
        return clientes;
    }

    /**
     * Retorna a lista de faturas.
     *
     * @return Lista de faturas.
     */
    public List<Fatura> getFaturas() {
        return faturas;
    }

    /**
     * Salva os dados de clientes e faturas no ficheiro binário.
     */
    public void salvarDados() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHEIRO_OBJETOS))) {
            oos.writeObject(clientes);
            oos.writeObject(faturas);
            System.out.println("Dados salvos com sucesso no ficheiro de objetos.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    /**
     * Carrega os dados de clientes e faturas do ficheiro binário.
     * Se o ficheiro não existir, cria um ficheiro vazio.
     */
    public void carregarDados() {
        File ficheiro = new File(FICHEIRO_OBJETOS);

        // Verifica se o ficheiro existe
        if (!ficheiro.exists()) {
            System.out.println("Ficheiro de objetos não encontrado. Criando um novo ficheiro.");
            salvarDados();
            return;
        }

        // Tenta carregar os dados do ficheiro
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FICHEIRO_OBJETOS))) {
            clientes = (List<Cliente>) ois.readObject();
            faturas = (List<Fatura>) ois.readObject();
            System.out.println("Dados carregados com sucesso do ficheiro de objetos.");
        } catch (IOException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Erro ao carregar os dados: Classe não encontrada - " + e.getMessage());
        }
    }
}
