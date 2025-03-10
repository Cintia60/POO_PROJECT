/**
 * Classe que representa um produto de farmácia.
 * Estende a classe Produto e inclui características específicas como prescrição, categoria e médico.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */
public class ProdutoFarmacia extends Produto {
    private static final long serialVersionUID = 1L;

    // Atributos específicos de produtos de farmácia
    private boolean comPrescricao;
    private String categoria;
    private String medico;

    /**
     * Construtor para inicializar um produto de farmácia.
     *
     * @param codigo         Código do produto.
     * @param nome           Nome do produto.
     * @param descricao      Descrição do produto.
     * @param valorUnitario  Valor unitário do produto (sem IVA).
     * @param comPrescricao  Indica se o produto necessita de prescrição médica.
     * @param categoria      Categoria do produto (para produtos sem prescrição).
     * @param medico         Nome do médico (para produtos com prescrição).
     * @param quantidade     Quantidade disponível do produto.
     * @param tipoTaxa       Tipo de taxa (não utilizado, mas necessário para compatibilidade).
     */
    public ProdutoFarmacia(String codigo, String nome, String descricao, double valorUnitario,
                           boolean comPrescricao, String categoria, String medico, int quantidade, String tipoTaxa) {
        super(codigo, nome, descricao, valorUnitario, tipoTaxa, quantidade);
        this.comPrescricao = comPrescricao;

        if (comPrescricao) {
            // Produtos com prescrição devem ter o nome do médico informado
            if (medico == null || medico.trim().isEmpty()) {
                throw new IllegalArgumentException("Produtos com prescrição devem informar o nome do médico.");
            }
            this.medico = medico;
            this.categoria = null;
        } else {
            // Validação da categoria para produtos sem prescrição
            if (!isValidCategoria(categoria)) {
                throw new IllegalArgumentException("Categoria inválida. Use 'Beleza', 'Bem-estar', 'Bebês', 'Animais' ou 'Outro'.");
            }
            this.categoria = categoria;
            this.medico = null;
        }
    }

    // Getters e Setters

    public boolean isComPrescricao() {
        return comPrescricao;
    }

    public void setComPrescricao(boolean comPrescricao) {
        this.comPrescricao = comPrescricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        if (!isValidCategoria(categoria)) {
            throw new IllegalArgumentException("Categoria inválida. Use 'Beleza', 'Bem-estar', 'Bebês', 'Animais' ou 'Outro'.");
        }
        this.categoria = categoria;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        if (medico == null || medico.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do médico é obrigatório para produtos com prescrição.");
        }
        this.medico = medico;
    }

    /**
     * Valida se a categoria fornecida é válida.
     *
     * @param categoria Categoria do produto.
     * @return true se a categoria for válida, false caso contrário.
     */
    private boolean isValidCategoria(String categoria) {
        return categoria.equalsIgnoreCase("Beleza") ||
                categoria.equalsIgnoreCase("Bem-estar") ||
                categoria.equalsIgnoreCase("Bebês") ||
                categoria.equalsIgnoreCase("Animais") ||
                categoria.equalsIgnoreCase("Outro");
    }

    @Override
    public String getTipo() {
        return "Farmacia";
    }

    @Override
    public boolean isTipoTaxaObrigatorio() {
        return false; // Tipo de taxa não é necessário para produtos de farmácia
    }

    @Override
    public String getDetalhesFatura(String tipoLocalizacao) {
        if (comPrescricao) {
            return "Prescrição médica: " + (medico != null ? medico : "N/A");
        }
        return "Categoria: " + (categoria != null ? categoria : "N/A");
    }

    /**
     * Calcula o imposto aplicável ao produto com base na localização e características específicas.
     *
     * @param tipoLocalizacao Tipo de localização do cliente ("Continente", "Madeira" ou "Açores").
     * @return Valor do imposto aplicável ao produto.
     */
    @Override
    public double calcularImposto(String tipoLocalizacao) {
        double taxaBase;

        // Determina a taxa base de imposto com base na localização e prescrição
        switch (tipoLocalizacao) {
            case "Continente" -> taxaBase = comPrescricao ? 6 : 23;
            case "Madeira" -> taxaBase = comPrescricao ? 5 : 23;
            case "Açores" -> taxaBase = comPrescricao ? 4 : 23;
            default -> throw new IllegalArgumentException("Tipo de localização inválido.");
        }

        // Redução de 1% para a categoria "Animais" (sem prescrição)
        if (!comPrescricao && categoria.equalsIgnoreCase("Animais")) {
            taxaBase -= 1;
        }

        // Garante que a taxa base nunca seja negativa
        taxaBase = Math.max(taxaBase, 0);

        // Calcula o imposto total
        return (getValorUnitario() * getQuantidade() * taxaBase) / 100;
    }

    /**
     * Representação textual do produto de farmácia.
     *
     * @return String contendo os atributos do produto.
     */
    @Override
    public String toString() {
        return "ProdutoFarmacia{" +
                "codigo='" + getCodigo() + '\'' +
                ", nome='" + getNome() + '\'' +
                ", descricao='" + getDescricao() + '\'' +
                ", valorUnitario=" + getValorUnitario() +
                ", tipoTaxa='" + getTipoTaxa() + '\'' +
                ", comPrescricao=" + comPrescricao +
                ", categoria='" + (categoria != null ? categoria : "N/A") + '\'' +
                ", medico='" + (medico != null ? medico : "N/A") + '\'' +
                ", quantidade=" + getQuantidade() +
                '}';
    }
}
