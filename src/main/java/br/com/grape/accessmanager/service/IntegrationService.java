package br.com.grape.accessmanager.service;

import br.com.grape.accessmanager.dto.integration.IntegrationDTOs.*;
import br.com.grape.accessmanager.entity.User;
import java.util.List;

public interface IntegrationService {
    GeneratedTokenResponse generateTokenForMember(GenerateTokenRequest request, User adminUser, String ipAddress);
    TokenMetadataResponse revokeToken(Integer accessId, User adminUser, String ipAddress);
    List<TokenMetadataResponse> listCompanyTokens(User adminUser);
}