package br.com.autoservice;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SegurancaUtil {

    /** Calcula SHA-256 e retorna em hex minúsculo (64 chars). */
    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao calcular SHA-256", e);
        }
    }

    /** Verifica a senha pura contra o hash salvo (SHA-256 em hex). */
    public static boolean verificarSenha(String senhaPura, String hashArmazenado) {
        if (senhaPura == null || hashArmazenado == null) return false;
        // normaliza: remove espaços/quebras e converte para minúsculo
        String hashBanco = hashArmazenado.replaceAll("\\s+", "").trim().toLowerCase();
        String shaCalc   = sha256Hex(senhaPura).toLowerCase();
        return shaCalc.equals(hashBanco);
    }
}
