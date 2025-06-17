package br.com.autoservice;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private double valor;

    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "carro_id", nullable = false)
    private Carro carro;
    
    @Column(nullable = false)
    private int quilometragem;


    public Servico() {}

    public Servico(String descricao, double valor, LocalDate data,int quilometragem, Carro carro) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.quilometragem = quilometragem;
        this.carro = carro;
    }

    // Getters e Setters
    public Long getId() { 
        return id; 
    }

    public String getDescricao() { 
        return descricao; 
    }
    
    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }

    public double getValor() { 
        return valor; 
    }
    
    public void setValor(double valor) { 
        this.valor = valor; 
    }

    public LocalDate getData() { 
        return data; 
    }
    
    public void setData(LocalDate data) { 
        this.data = data; 
    }

    public Carro getCarro() { 
        return carro; 
    }
    
    public void setCarro(Carro carro) { 
        this.carro = carro; 
    }
    
    public int getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(int quilometragem) {
        this.quilometragem = quilometragem;
    }

}
