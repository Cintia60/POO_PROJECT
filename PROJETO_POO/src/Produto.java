import java.io.*;

/**
 * Classe abstrata que representa um Produto.
 * Cada produto possui código, nome, descrição, valor unitário sem IVA, tipo de taxa, e quantidade.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */
public abstract class Produto implements Serializable {
    private static final long serialVersionUID = 1L;

    // Atributos privados para encapsulamento
    private String codigo;
    private String nome;
    private String descricao;
    private double valorUnitario;
    private String tipoTaxa;
    private int quantidade;

    /**
     * Construtor para inicializar os atributos de um produto.
     *
     * @param codigo        Código único do produto.
     * @param nome          Nome do produto.
     * @param descricao     Descrição do produto.
     * @param valorUnitario Valor unitário sem IVA (deve ser positivo).
     * @param tipoTaxa      Tipo de taxa ("Taxa reduzida", "Taxa intermédia", "Taxa normal").
     * @param quantidade    Quantidade disponível (deve ser não negativa).
     */
    public Produto(String codigo, String nome, String descricao, double valorUnitario, String tipoTaxa, int quantidade) {
        setCodigo(codigo);
        setNome(nome);
        setDescricao(descricao);
        setValorUnitario(valorUnitario);
        if (isTipoTaxaObrigatorio()) {
            setTipoTaxa(tipoTaxa);
        } else {
            this.tipoTaxa = null;
        }
        setQuantidade(quantidade);
    }

    // Métodos abstratos a serem implementados pelas subclasses

    /**
     * Verifica se o tipo de taxa é obrigatório para o produto.
     *
     * @return true se o tipo de taxa for obrigatório, false caso contrário.
     */
    public abstract boolean isTipoTaxaObrigatorio();

    /**
     * Calcula o imposto do produto com base na localização.
     *
     * @param tipoLocalizacao Tipo de localização do cliente ("Continente", "Madeira", "Açores").
     * @return Valor do imposto aplicável ao produto.
     */
    public abstract double calcularImposto(String tipoLocalizacao);

    /**
     * Obtém a categoria do produto.
     *
     * @return A categoria do produto.
     */
    public abstract String getCategoria();

    /**
     * Obtém detalhes específicos do produto para exibição em uma fatura.
     *
     * @param tipoLocalizacao Tipo de localização do cliente.
     * @return String com detalhes específicos do produto.
     */
    public abstract String getDetalhesFatura(String tipoLocalizacao);

    /**
     * Obtém o tipo do produto.
     *
     * @return Tipo do produto.
     */
    public abstract String getTipo();

    // Getters e Setters com validações

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código do produto não pode ser nulo ou vazio.");
        }
        this.codigo = codigo.trim();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto não pode ser nulo ou vazio.");
        }
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do produto não pode ser nula ou vazia.");
        }
        this.descricao = descricao.trim();
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    /**
     * Define o valor unitário do produto.
     *
     * @param valorUnitario Valor unitário sem IVA (deve ser positivo).
     * @throws IllegalArgumentException se o valor for inválido.
     */
    public void setValorUnitario(double valorUnitario) {
        if (valorUnitario <= 0) {
            throw new IllegalArgumentException("O valor unitário deve ser maior que zero.");
        }
        this.valorUnitario = valorUnitario;
    }

    public String getTipoTaxa() {
        return tipoTaxa;
    }

    /**
     * Define o tipo de taxa do produto.
     *
     * @param tipoTaxa Tipo de taxa ("Taxa reduzida", "Taxa intermédia", "Taxa normal").
     * @throws IllegalArgumentException se o tipo de taxa for inválido.
     */
    public void setTipoTaxa(String tipoTaxa) {
        if (tipoTaxa == null) {
            throw new IllegalArgumentException("O tipo de taxa não pode ser nulo.");
        }
        if (!tipoTaxa.equalsIgnoreCase("Taxa reduzida") &&
                !tipoTaxa.equalsIgnoreCase("Taxa intermédia") &&
                !tipoTaxa.equalsIgnoreCase("Taxa normal")) {
            throw new IllegalArgumentException("Tipo de taxa inválido. Use 'Taxa reduzida', 'Taxa intermédia' ou 'Taxa normal'.");
        }
        this.tipoTaxa = tipoTaxa;
    }

    public int getQuantidade() {
        return quantidade;
    }

    /**
     * Define a quantidade disponível do produto.
     *
     * @param quantidade Quantidade disponível (deve ser não negativa).
     * @throws IllegalArgumentException se a quantidade for inválida.
     */
    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior ou igual a zero.");
        }
        this.quantidade = quantidade;
    }

    /**
     * Calcula o valor total de IVA para o produto.
     *
     * @param tipoLocalizacao Tipo de localização do cliente ("Continente", "Madeira", "Açores").
     * @return Valor total do IVA.
     */
    public double calcularTotalIVA(String tipoLocalizacao) {
        return calcularImposto(tipoLocalizacao) * quantidade;
    }

    /**
     * Representação textual do produto.
     *
     * @return String com os atributos do produto.
     */
    @Override
    public String toString() {
        return "Produto{" +
                "codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", valorUnitario=" + valorUnitario +
                ", tipoTaxa='" + tipoTaxa + '\'' +
                ", quantidade=" + quantidade +
                '}';
    }
}
