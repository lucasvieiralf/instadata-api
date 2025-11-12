package br.com.grape.accessmanager.dto.integration;

import java.time.Instant;

import br.com.grape.accessmanager.entity.MemberPlatformAccess;
import br.com.grape.accessmanager.entity.Platform;
import br.com.grape.accessmanager.enums.PlatformAccessStatus;

public class IntegrationDTOs {

    /**
     * Request para gerar um novo token de acesso para um membro.
     */
    public record GenerateTokenRequest(
            Integer memberId, // O ID do CompanyMember (ex: "Shin")
            Integer platformId // O ID da Platform (ex: "Nexos ERP")
    ) {
    }

    /**
     * Resposta com o token gerado.
     * Este é o único momento em que o token é mostrado!
     */
    public record GeneratedTokenResponse(
            Integer accessId,
            String accessToken, // O token (nex_live_...)
            Integer memberId,
            String memberName,
            Integer platformId,
            String platformName,
            PlatformAccessStatus status) {
        public static GeneratedTokenResponse fromEntity(MemberPlatformAccess access, String plainTextToken) {
            return new GeneratedTokenResponse(
                    access.getId(),
                    plainTextToken, // O token em texto puro!
                    access.getCompanyMember().getId(),
                    access.getCompanyMember().getUser().getName(),
                    access.getPlatform().getId(),
                    access.getPlatform().getPlatformName(),
                    access.getStatus());
        }
    }

    /**
     * Resposta para listagem de tokens (NUNCA mostra o token, só os metadados).
     */
    public record TokenMetadataResponse(
            Integer accessId,
            Integer memberId,
            String memberName,
            Integer platformId,
            String platformName,
            PlatformAccessStatus status,
            Instant createdAt) {
        public static TokenMetadataResponse fromEntity(MemberPlatformAccess access) {
            return new TokenMetadataResponse(
                    access.getId(),
                    access.getCompanyMember().getId(),
                    access.getCompanyMember().getUser().getName(),
                    access.getPlatform().getId(),
                    access.getPlatform().getPlatformName(),
                    access.getStatus(),
                    access.getCreatedAt());
        }
    }

    public record AccessListResponse(
            Integer accessId,
            String memberName,
            String platformName,
            PlatformAccessStatus status) {
        public static AccessListResponse fromEntity(MemberPlatformAccess access) {
            return new AccessListResponse(
                    access.getId(),
                    access.getCompanyMember().getUser().getName(),
                    access.getPlatform().getPlatformName(),
                    access.getStatus());
        }
    }

    public record PlatformListResponse(
            Integer platformId,
            String platformName,
            String platformKey) {
        public static PlatformListResponse fromEntity(Platform platform) {
            return new PlatformListResponse(
                    platform.getId(),
                    platform.getPlatformName(),
                    platform.getPlatformKey());
        }
    }

    public record AccessDetailResponse(
            Integer accessId,
            String memberName,
            Integer memberId,
            String platformName,
            Integer platformId,
            PlatformAccessStatus status) {
        public static AccessDetailResponse fromEntity(MemberPlatformAccess access) {
            return new AccessDetailResponse(
                    access.getId(),
                    access.getCompanyMember().getUser().getName(),
                    access.getCompanyMember().getId(),
                    access.getPlatform().getPlatformName(),
                    access.getPlatform().getId(),
                    access.getStatus());
        }
    }
}