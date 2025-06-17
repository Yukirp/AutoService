package br.com.autoservice;

import java.security.MessageDigest;

public class SegurancaUtil {

    public static String criptografar(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }
}
