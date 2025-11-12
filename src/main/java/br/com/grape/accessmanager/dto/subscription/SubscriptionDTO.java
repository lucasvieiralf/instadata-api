package br.com.grape.accessmanager.dto.subscription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import br.com.grape.accessmanager.entity.Plan;
import br.com.grape.accessmanager.entity.PlanLimit;
import br.com.grape.accessmanager.entity.Subscription;
import br.com.grape.accessmanager.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTOs para o fluxo de Assinatura (lado do Cliente/CompanyAdmin).
 */
public class SubscriptionDTO {

    /**
     * Resposta de um Limite/Feature de um plano (visão do cliente).
     */
    public record PublicPlanLimitResponse(
        String featureName,
        String limitValue
    ) {
        public static PublicPlanLimitResponse fromEntity(PlanLimit limit) {
            return new PublicPlanLimitResponse(
                limit.getFeature().getDisplayName(),
                limit.getLimitValue()
            );
        }
    }

    /**
     * Resposta de um Plano disponível para assinatura (visão do cliente).
     */
    public record PublicPlanResponse(
        Integer id,
        String planName,
        BigDecimal monthlyPrice,
        List<PublicPlanLimitResponse> limits
    ) {
        public static PublicPlanResponse fromEntity(Plan plan) {
            List<PublicPlanLimitResponse> limitDTOs = plan.getPlanLimits().stream()
                .map(PublicPlanLimitResponse::fromEntity)
                .collect(Collectors.toList());
            
            return new PublicPlanResponse(
                plan.getId(),
                plan.getPlanName(),
                plan.getMonthlyPrice(),
                limitDTOs
            );
        }
    }

    /**
     * Resposta da Assinatura ATUAL da empresa.
     */
    public record CurrentSubscriptionResponse(
        Integer subscriptionId,
        SubscriptionStatus status,
        LocalDate startsAt,
        LocalDate nextBillingAt,
        PublicPlanResponse plan
    ) {
        public static CurrentSubscriptionResponse fromEntity(Subscription subscription) {
            return new CurrentSubscriptionResponse(
                subscription.getId(),
                subscription.getStatus(),
                subscription.getStartsAt(),
                subscription.getNextBillingAt(),
                PublicPlanResponse.fromEntity(subscription.getPlan())
            );
        }
    }

    /**
     * Request para criar uma nova assinatura.
     */
    public record SubscribeRequest(
        @NotNull
        Integer planId
    ) {}

}