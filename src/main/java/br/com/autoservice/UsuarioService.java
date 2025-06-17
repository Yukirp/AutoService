package br.com.autoservice;

import java.util.List;

public class UsuarioService {

    // Método fictício para buscar a senha de um usuário
    public static String buscarSenhaUsuarioLogado() {
        // Aqui você deveria buscar a senha criptografada no banco de dados
        // Para o exemplo, estou retornando uma senha "fictícia"
        return "senhaDoUsuarioCadastrado";  // Alterar conforme necessário
    }

    // Método para verificar a senha, usando uma comparação simples para exemplo
    public static boolean verificarSenhaUsuario(String senhaDigitada, String senhaArmazenada) {
        // A comparação deve ser feita de forma segura, e idealmente usando criptografia
        return senhaDigitada.equals(senhaArmazenada); // Use bcrypt ou outra forma de comparação segura
    }
}
