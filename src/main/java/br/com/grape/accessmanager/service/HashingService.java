package br.com.grape.accessmanager.service;

import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Serviço utilitário para criar hashes de tokens.
 * Usamos SHA-256 (rápido) para tokens de API, em vez de BCrypt (lento) de senhas.
 */
@Service
public class HashingService {

    /**
     * Calcula o hash SHA-256 de um token em texto puro.
     * @param plainTextToken O token (base: "nex_live_...")
     * @return O hash SHA-256 em formato hexadecimal (64 caracteres)
     */
    public String hashSha256(String plainTextToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainTextToken.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Falha interna ao calcular hash: Algoritmo SHA-256 não encontrado", e);
        }
    }

    /**
     * Converte um array de bytes em uma string hexadecimal.
     */
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}