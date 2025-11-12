package br.com.grape.accessmanager.dto.admin;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.grape.accessmanager.entity.Plan;
import br.com.grape.accessmanager.entity.PlanLimit;
import br.com.grape.accessmanager.enums.PlanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTOs para gerenciamento de Planos (Plans)
 */
public class PlanManagementDTOs {

    /**
     * Request para criar um novo Plano.
     */
    public record CreatePlanRequest(
        @NotBlank
        String planName,

        @NotNull @DecimalMin(value = "0.0")
        BigDecimal monthlyPrice,

        @NotNull
        PlanStatus status
    ) {}

    /**
     * Request para adicionar um limite (PlanLimit) a um Plano.
     */
    public record AddLimitRequest(
        @NotBlank
        String featureKey, // Ex: "MAX_USERS"

        @NotBlank
        String limitValue  // Ex: "10"
    ) {}

    /**
     * Resposta para um limite de plano.
     */
    public record PlanLimitResponse(
        Integer id,
        String featureKey,
        String limitValue,
        String featureDisplayName
    ) {
        public static PlanLimitResponse fromEntity(PlanLimit limit) {
            return new PlanLimitResponse(
                limit.getId(),
                limit.getFeature().getFeatureKey(),
                limit.getLimitValue(),
                limit.getFeature().getDisplayName()
            );
        }
    }

    /**
     * Resposta completa de um Plano, incluindo seus limites.
     */
    public record PlanResponse(
        Integer id,
        String planName,
        BigDecimal monthlyPrice,
        PlanStatus status,
        Set<PlanLimitResponse> limits
    ) {
        public static PlanResponse fromEntity(Plan plan) {
            return new PlanResponse(
                plan.getId(),
                plan.getPlanName(),
                plan.getMonthlyPrice(),
                plan.getStatus(),
                plan.getPlanLimits().stream()
                    .map(PlanLimitResponse::fromEntity)
                    .collect(Collectors.toSet())
            );
        }
    }
}