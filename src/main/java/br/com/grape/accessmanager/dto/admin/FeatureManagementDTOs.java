package br.com.grape.accessmanager.dto.admin;

import br.com.grape.accessmanager.entity.Feature;
import br.com.grape.accessmanager.enums.FeatureDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTOs para gerenciamento de Features (Funcionalidades)
 */
public class FeatureManagementDTOs {

    /**
     * Request para criar uma nova Feature no sistema.
     */
    public record CreateFeatureRequest(
        @NotBlank @Size(min = 3, max = 50)
        String featureKey,

        @NotBlank @Size(min = 3, max = 100)
        String displayName,

        @NotNull
        FeatureDataType dataType
    ) {}

    /**
     * Resposta padrão ao retornar uma Feature.
     */
    public record FeatureResponse(
        Integer id,
        String featureKey,
        String displayName,
        FeatureDataType dataType
    ) {
        public static FeatureResponse fromEntity(Feature feature) {
            return new FeatureResponse(
                feature.getId(),
                feature.getFeatureKey(),
                feature.getDisplayName(),
                feature.getDataType()
            );
        }
    }
}