package br.com.autoservice;

import javax.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    // mapeia o atributo 'senha' da classe para a coluna 'senhaHash' do banco
    @Column(name = "senhaHash", nullable = false)
    private String senha;

    public Usuario() {}

    public Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    // Getters e Setters
    public Long getId() { 
        return id; 
    }

    public String getLogin() { 
        return login; 
    }

    public void setLogin(String login) { 
        this.login = login; 
    }

    public String getSenha() { 
        return senha; 
    }

    public void setSenha(String senha) { 
        this.senha = senha; 
    }
}
