package br.com.autoservice;

import javax.persistence.*;

@Entity
@Table(name = "carros")
public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placa;
    private String cliente;
    private String marca;
    private String modelo;
    
    @Column(nullable = false)
    private boolean ativo = true;  // padrão: true

    private int ano;

    // Construtores
    public Carro() {}

    public Carro(String placa, String marca, String modelo, int ano, String cliente) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cliente = cliente;
        this.ativo = true;
    }

    // Getters e Setters
    public Long getId() { return id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
