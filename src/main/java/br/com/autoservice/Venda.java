package br.com.autoservice;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Produto produto;

    private int quantidade;
    private String comprador;
    private Double valorTotal;
    
     @Temporal(TemporalType.TIMESTAMP)
    private Date dataVenda = new Date();

    // getter e setter
    public Date getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Venda() {}

    public Venda(Produto produto, int quantidade, String comprador, Double valorTotal) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.comprador = comprador;
        this.valorTotal = valorTotal;
    }

    // Getters e setters
    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getComprador() {
        return comprador;
    }

    public void setComprador(String comprador) {
        this.comprador = comprador;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
