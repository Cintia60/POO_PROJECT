import java.io.*;

/**
 * Classe que representa um cliente.
 * Cada cliente possui nome, localização e número de contribuinte.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    // Atributos privados
    private String nome;
    private String tipoLocalizacao;
    private int numeroContribuinte;

    /**
     * Construtor para inicializar os atributos de um cliente.
     *
     * @param nome Nome do cliente.
     * @param tipoLocalizacao Tipo de localização do cliente ("Continente", "Madeira" ou "Açores").
     * @param numeroContribuinte Número de contribuinte (inteiro positivo, 4 dígitos).
     * @throws IllegalArgumentException se os parâmetros forem inválidos.
     */
    public Cliente(String nome, String tipoLocalizacao, int numeroContribuinte) {
        setNome(nome);
        setTipoLocalizacao(tipoLocalizacao);
        setNumeroContribuinte(numeroContribuinte);
    }

    // Getters e Setters

    /**
     * Obtém o nome do cliente.
     *
     * @return O nome do cliente.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do cliente.
     *
     * @param nome Nome do cliente.
     * @throws IllegalArgumentException se o nome for vazio ou nulo.
     */
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio ou nulo.");
        }
        this.nome = nome.trim();
    }

    /**
     * Obtém o tipo de localização do cliente.
     *
     * @return Tipo de localização do cliente ("Continente", "Madeira" ou "Açores").
     */
    public String getTipoLocalizacao() {
        return tipoLocalizacao;
    }

    /**
     * Define o tipo de localização do cliente.
     *
     * @param tipoLocalizacao Tipo de localização ("Continente", "Madeira" ou "Açores").
     * @throws IllegalArgumentException se a localização for inválida ou nula.
     */
    public void setTipoLocalizacao(String tipoLocalizacao) {
        if (tipoLocalizacao == null || tipoLocalizacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de localização não pode ser nulo ou vazio.");
        }

        String localizacaoLimpa = tipoLocalizacao.trim();

        if (!localizacaoLimpa.equalsIgnoreCase("Continente") &&
                !localizacaoLimpa.equalsIgnoreCase("Madeira") &&
                !localizacaoLimpa.equalsIgnoreCase("Açores")) {
            throw new IllegalArgumentException("Tipo de localização inválido. Use 'Continente', 'Madeira' ou 'Açores'.");
        }

        this.tipoLocalizacao = localizacaoLimpa;
    }

    /**
     * Obtém o número de contribuinte do cliente.
     *
     * @return Número de contribuinte do cliente.
     */
    public int getNumeroContribuinte() {
        return numeroContribuinte;
    }

    /**
     * Define o número de contribuinte do cliente.
     *
     * @param numeroContribuinte Número de contribuinte (deve ser positivo e conter 4 dígitos).
     * @throws IllegalArgumentException se o número for inválido.
     */
    public void setNumeroContribuinte(int numeroContribuinte) {
        if (numeroContribuinte <= 999 || numeroContribuinte > 9999) {
            throw new IllegalArgumentException("Número de contribuinte deve conter exatamente 4 dígitos.");
        }
        this.numeroContribuinte = numeroContribuinte;
    }

    /**
     * Retorna uma representação textual do cliente.
     *
     * @return Uma string contendo os atributos do cliente.
     */
    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", tipoLocalizacao='" + tipoLocalizacao + '\'' +
                ", numeroContribuinte=" + numeroContribuinte +
                '}';
    }
}
