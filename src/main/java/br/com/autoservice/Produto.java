package br.com.autoservice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Double preco;
    private String marca;
    private String veiculosCompativeis;
    private String descricao;
    private int quantidade;

    public Produto() {}

    public Produto(String nome, Double preco, String marca, String veiculosCompativeis, String descricao, int quantidade) {
        this.nome = nome;
        this.preco = preco;
        this.marca = marca;
        this.veiculosCompativeis = veiculosCompativeis;
        this.descricao = descricao;
        this.quantidade = quantidade;
    }

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getVeiculosCompativeis() {
        return veiculosCompativeis;
    }

    public void setVeiculosCompativeis(String veiculosCompativeis) {
        this.veiculosCompativeis = veiculosCompativeis;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
