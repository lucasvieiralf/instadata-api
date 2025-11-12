package br.com.grape.accessmanager.dto.admin;

import br.com.grape.accessmanager.entity.Platform;
import br.com.grape.accessmanager.enums.PlatformStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PlatformManagementDTOs {

    public record CreatePlatformRequest(
        @NotBlank @Size(max = 150)
        String platformName,
        
        @NotBlank @Size(max = 50)
        String platformKey, // Ex: "NEXOS_ERP"
        
        @NotNull
        PlatformStatus status
    ) {}

    public record PlatformResponse(
        Integer id,
        String platformName,
        String platformKey,
        PlatformStatus status
    ) {
        public static PlatformResponse fromEntity(Platform platform) {
            return new PlatformResponse(
                platform.getId(),
                platform.getPlatformName(),
                platform.getPlatformKey(),
                platform.getStatus()
            );
        }
    }
}