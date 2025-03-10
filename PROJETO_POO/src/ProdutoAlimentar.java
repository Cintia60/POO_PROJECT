import java.util.*;

/**
 * Classe que representa um produto alimentar.
 * Inclui informações como categoria, status biológico e certificações.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */
public class ProdutoAlimentar extends Produto {
    private static final long serialVersionUID = 1L;

    // Atributos específicos de produtos alimentares
    private String categoria;
    private boolean biologico;
    private List<String> certificacoes;
    private final List<String> certificacoesValidas = List.of("ISO22000", "FSSC22000", "HACCP", "GMP"); // Certificações aceitas

    /**
     * Construtor para inicializar os atributos do produto alimentar.
     *
     * @param codigo          Código do produto.
     * @param nome            Nome do produto.
     * @param descricao       Descrição do produto.
     * @param valorUnitario   Valor unitário (sem IVA).
     * @param tipoTaxa        Tipo de taxa ("Taxa reduzida", "Taxa intermédia", "Taxa normal").
     * @param biologico       Indica se o produto é biológico.
     * @param certificacoes   Lista de certificações específicas (apenas para Taxa Reduzida).
     * @param categoria       Categoria do produto (apenas para Taxa Intermédia).
     * @param quantidade      Quantidade disponível do produto.
     */
    public ProdutoAlimentar(String codigo, String nome, String descricao, double valorUnitario,
                            String tipoTaxa, boolean biologico, List<String> certificacoes,
                            String categoria, int quantidade) {
        super(codigo, nome, descricao, valorUnitario, tipoTaxa, quantidade);
        this.certificacoes = new ArrayList<>();
        this.biologico = biologico;
        setCertificacoes(certificacoes);
        setCategoria(categoria);
    }

    /**
     * Obtém o número de certificações do produto.
     *
     * @return Número de certificações.
     */
    public int getNumCertificacoes() {
        return certificacoes.size();
    }

    /**
     * Obtém a lista de certificações do produto.
     *
     * @return Lista de certificações.
     */
    public List<String> getCertificacoes() {
        return certificacoes;
    }

    /**
     * Define as certificações do produto com base no tipo de taxa.
     *
     * @param certificacoes Lista de certificações.
     * @throws IllegalArgumentException Se as certificações forem inválidas para o tipo de taxa.
     */
    public void setCertificacoes(List<String> certificacoes) {
        if (getTipoTaxa().equalsIgnoreCase("Taxa reduzida")) {
            if (certificacoes == null || certificacoes.size() < 1 || certificacoes.size() > 4) {
                throw new IllegalArgumentException("Produtos com Taxa Reduzida devem ter entre 1 e 4 certificações.");
            }
            for (String certificacao : certificacoes) {
                if (!certificacoesValidas.contains(certificacao)) {
                   //throw new IllegalArgumentException("Certificação inválida: " + certificacao);
                }
            }
            this.certificacoes = new ArrayList<>(certificacoes);
        } else if (getTipoTaxa().equalsIgnoreCase("Taxa intermédia") || getTipoTaxa().equalsIgnoreCase("Taxa normal")) {
            // Remove certificações automaticamente para produtos com Taxa Intermédia ou Normal
            if (certificacoes != null && !certificacoes.isEmpty()) {
                //System.out.println("Certificações removidas: Produtos com Taxa Intermédia ou Normal não podem ter certificações.");
            }
            this.certificacoes = new ArrayList<>();
        } else {
            throw new IllegalArgumentException("Tipo de taxa desconhecido.");
        }
    }

    public String getCategoria() {
        return categoria;
    }

    /**
     * Define a categoria do produto com base no tipo de taxa.
     *
     * @param categoria Categoria do produto.
     * @throws IllegalArgumentException Se a categoria for inválida para o tipo de taxa.
     */
    public void setCategoria(String categoria) {
        if (getTipoTaxa().equalsIgnoreCase("Taxa reduzida")) {
            this.categoria = null;
        } else if (getTipoTaxa().equalsIgnoreCase("Taxa intermédia")) {
            if (!categoria.equalsIgnoreCase("congelados") &&
                    !categoria.equalsIgnoreCase("enlatados") &&
                    !categoria.equalsIgnoreCase("vinho")) {
                throw new IllegalArgumentException("Categoria inválida para produtos com Taxa Intermédia.");
            }
            this.categoria = categoria;
        } else if (getTipoTaxa().equalsIgnoreCase("Taxa normal")) {
            if (categoria != null && !categoria.trim().isEmpty()) {
                throw new IllegalArgumentException("Produtos com Taxa Normal não podem ter categorias específicas.");
            }
            this.categoria = null;
        } else {
            throw new IllegalArgumentException("Tipo de taxa desconhecido.");
        }
    }

    public boolean isBiologico() {
        return biologico;
    }

    public void setBiologico(boolean biologico) {
        this.biologico = biologico;
    }

    @Override
    public String getTipo() {
        return "Alimentar";
    }

    @Override
    public boolean isTipoTaxaObrigatorio() {
        return true;
    }

    @Override
    public String getDetalhesFatura(String tipoLocalizacao) {
        return "Biológico: " + (isBiologico() ? "Sim" : "Não") +
                ", Certificações: " + getNumCertificacoes();
    }

    @Override
    public double calcularImposto(String tipoLocalizacao) {
        double taxaBase = switch (tipoLocalizacao) {
            case "Continente" -> switch (getTipoTaxa()) {
                case "Taxa reduzida" -> 6;
                case "Taxa intermédia" -> 13;
                case "Taxa normal" -> 23;
                default -> 0;
            };
            case "Madeira" -> switch (getTipoTaxa()) {
                case "Taxa reduzida" -> 5;
                case "Taxa intermédia" -> 12;
                case "Taxa normal" -> 22;
                default -> 0;
            };
            case "Açores" -> switch (getTipoTaxa()) {
                case "Taxa reduzida" -> 4;
                case "Taxa intermédia" -> 9;
                case "Taxa normal" -> 16;
                default -> 0;
            };
            default -> throw new IllegalArgumentException("Tipo de localização inválido.");
        };

        if (isBiologico()) {
            taxaBase -= taxaBase * 0.10; // Desconto de 10% se for biológico
        }
        if (certificacoes.size() == 4) {
            taxaBase -= 1; // Redução de 1% se tiver 4 certificações
        }
        if ("vinho".equalsIgnoreCase(categoria)) {
            taxaBase += 1; // Aumento de 1% se for vinho
        }

        taxaBase = Math.max(taxaBase, 0);
        return (getValorUnitario() * getQuantidade() * taxaBase) / 100;
    }

    @Override
    public String toString() {
        return "ProdutoAlimentar{" +
                "codigo='" + getCodigo() + '\'' +
                ", nome='" + getNome() + '\'' +
                ", descricao='" + getDescricao() + '\'' +
                ", valorUnitario=" + getValorUnitario() +
                ", tipoTaxa='" + getTipoTaxa() + '\'' +
                ", biologico=" + biologico +
                ", certificacoes=" + certificacoes +
                ", categoria='" + categoria + '\'' +
                ", quantidade=" + getQuantidade() +
                '}';
    }
}
