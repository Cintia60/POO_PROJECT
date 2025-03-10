import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;
import java.util.*;

/**
 * Classe que representa uma fatura.
 * A fatura associa um cliente a uma lista de produtos adquiridos,
 * além de calcular os valores totais com e sem IVA.
 *
 * @author Cíntia Cumbane (2020244607)
 * Cristiana Gonçalves (2019239753)
 * @version 3.0
 */
public class Fatura implements Serializable {
    private static final long serialVersionUID = 1L;

    // Atributos privados
    private int numero; // Número identificador único da fatura
    private Cliente cliente; // Cliente associado à fatura
    private List<Produto> produtos; // Lista de produtos da fatura
    private double valorTotalSemIVA; // Valor total sem IVA
    private double valorTotalIva; // Valor total de IVA
    private double valorTotalComIVA; // Valor total com IVA
    private LocalDate data; // Data de emissão da fatura
    private transient Scanner scanner; // Scanner para entrada de dados (não serializado)

    /**
     * Construtor para inicializar os atributos de uma fatura.
     *
     * @param numero Número identificador único da fatura.
     * @param cliente Cliente associado à fatura.
     * @param data Data de emissão da fatura.
     */
    public Fatura(int numero, Cliente cliente, LocalDate data) {
        this.numero = numero;
        this.cliente = cliente;
        this.data = data;
        this.produtos = new ArrayList<>();
        this.valorTotalSemIVA = 0;
        this.valorTotalIva = 0;
        this.valorTotalComIVA = 0;
        this.scanner = new Scanner(System.in); // Scanner inicializado para entradas futuras
    }

    /**
     * Adiciona um produto à lista de produtos da fatura.
     * Após adicionar, recalcula os valores totais da fatura.
     *
     * @param produto Produto a ser adicionado à fatura.
     */
    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
        recalcularValores();
    }

    /**
     * Recalcula os valores totais da fatura com base nos produtos incluídos.
     */
    private void recalcularValores() {
        valorTotalSemIVA = 0;
        valorTotalIva = 0;
        valorTotalComIVA = 0;

        for (Produto produto : produtos) {
            double precoSemIVA = produto.getValorUnitario() * produto.getQuantidade();
            double imposto = produto.calcularImposto(cliente.getTipoLocalizacao());

            valorTotalSemIVA += precoSemIVA;
            valorTotalIva += imposto;
            valorTotalComIVA += precoSemIVA + imposto;
        }
    }

    // Getters e Setters

    /**
     * Obtém o número identificador da fatura.
     *
     * @return Número da fatura.
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Define o número identificador da fatura.
     *
     * @param numero Novo número da fatura.
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * Obtém o cliente associado à fatura.
     *
     * @return Cliente associado.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Define o cliente associado à fatura.
     * Recalcula os valores totais da fatura após alterar o cliente.
     *
     * @param cliente Novo cliente associado.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        recalcularValores();
    }

    /**
     * Obtém uma cópia imutável da lista de produtos da fatura.
     *
     * @return Lista de produtos da fatura.
     */
    public List<Produto> getProdutos() {
        return Collections.unmodifiableList(produtos);
    }

    /**
     * Substitui a lista de produtos da fatura e recalcula os valores totais.
     *
     * @param produtos Nova lista de produtos.
     */
    public void setProdutos(List<Produto> produtos) {
        this.produtos = new ArrayList<>(produtos);
        recalcularValores();
    }

    /**
     * Obtém o valor total da fatura sem IVA.
     *
     * @return Valor total sem IVA.
     */
    public double getValorTotalSemIVA() {
        return valorTotalSemIVA;
    }

    /**
     * Obtém o valor total de IVA da fatura.
     *
     * @return Valor total de IVA.
     */
    public double getValorTotalIva() {
        return valorTotalIva;
    }

    /**
     * Obtém o valor total da fatura com IVA.
     *
     * @return Valor total com IVA.
     */
    public double getValorTotalComIVA() {
        return valorTotalComIVA;
    }

    /**
     * Obtém a data de emissão da fatura.
     *
     * @return Data de emissão da fatura.
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Define a data de emissão da fatura.
     *
     * @param data Nova data de emissão.
     */
    public void setData(LocalDate data) {
        this.data = data;
    }

    /**
     * Lista os produtos da fatura como strings.
     *
     * @return Lista de strings representando os produtos.
     */
    public List<String> listarProdutos() {
        List<String> detalhesProdutos = new ArrayList<>();
        for (Produto produto : produtos) {
            detalhesProdutos.add(produto.toString());
        }
        return detalhesProdutos;
    }

    /**
     * Metodo auxiliar para desserializar o objeto.
     * Restaura o scanner após a desserialização.
     *
     * @param ois InputStream para leitura do objeto serializado.
     * @throws IOException            Erro de entrada/saída.
     * @throws ClassNotFoundException Classe não encontrada.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Representação textual da fatura.
     *
     * @return String contendo os atributos da fatura.
     */
    @Override
    public String toString() {
        return "Fatura{" +
                "numero=" + numero +
                ", cliente=" + cliente +
                ", produtos=" + produtos +
                ", valorTotalSemIVA=" + valorTotalSemIVA +
                ", valorTotalIva=" + valorTotalIva +
                ", valorTotalComIVA=" + valorTotalComIVA +
                ", data=" + data +
                '}';
    }
}
