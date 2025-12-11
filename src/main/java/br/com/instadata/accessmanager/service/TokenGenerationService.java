package br.com.instadata.accessmanager.service;

import org.springframework.stereotype.Service;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenGenerationService {

    private final HashingService hashingService;

    /**
     * Contêiner para o par de tokens.
     */
    public record GeneratedToken(String plainTextToken, String hashedToken) {}

    /**
     * Gera um par de tokens (texto puro e hash SHA-256).
     * @return Um objeto GeneratedToken contendo ambos.
     */
    public GeneratedToken generateApiToken() {
        // 1. Gera o token legível (texto puro)
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String plainTextToken = "nex_live_" + uuid;
        
        // 2. Calcula o hash
        String hashedToken = hashingService.hashSha256(plainTextToken);
        
        // 3. Retorna os dois
        return new GeneratedToken(plainTextToken, hashedToken);
    }
}