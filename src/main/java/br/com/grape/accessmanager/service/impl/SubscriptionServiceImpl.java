package br.com.grape.accessmanager.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.dto.subscription.SubscriptionDTO.*;
import br.com.grape.accessmanager.entity.Company;
import br.com.grape.accessmanager.entity.Plan;
import br.com.grape.accessmanager.entity.Subscription;
import br.com.grape.accessmanager.entity.User;
import br.com.grape.accessmanager.enums.PlanStatus;
import br.com.grape.accessmanager.enums.SubscriptionStatus;
import br.com.grape.accessmanager.repository.PlanRepository;
import br.com.grape.accessmanager.repository.SubscriptionRepository;
import br.com.grape.accessmanager.service.CompanyService;
import br.com.grape.accessmanager.service.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final CompanyService companyService;

    private static final List<SubscriptionStatus> ACTIVE_STATUSES = Arrays.asList(
            SubscriptionStatus.ACTIVE,
            SubscriptionStatus.TRIALING);

    @Override
    @Transactional(readOnly = true)
    public List<PublicPlanResponse> listAvailablePlans() {
        return planRepository.findByStatus(PlanStatus.ACTIVE).stream()
                .map(PublicPlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurrentSubscriptionResponse> getMySubscription(User adminUser) {
        Company adminCompany = companyService.getAdminCompany(adminUser);

        return subscriptionRepository.findByCompanyId(adminCompany.getId())
                .map(CurrentSubscriptionResponse::fromEntity); // Converte para DTO (se existir)
    }

    @Override
    public CurrentSubscriptionResponse createSubscription(SubscribeRequest request, User adminUser) {
        Company adminCompany = companyService.getAdminCompany(adminUser);

        subscriptionRepository.findByCompanyIdAndStatusIn(adminCompany.getId(), ACTIVE_STATUSES).ifPresent(sub -> {
            throw new RuntimeException("Empresa já possui uma assinatura ativa ou em trial.");
        });

        Plan chosenPlan = planRepository.findById(request.planId())
                .filter(plan -> plan.getStatus() == PlanStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Plano com ID " + request.planId() + " não encontrado ou inativo."));

        Subscription newSubscription = new Subscription();
        newSubscription.setCompany(adminCompany);
        newSubscription.setPlan(chosenPlan);
        newSubscription.setStatus(SubscriptionStatus.TRIALING);
        newSubscription.setStartsAt(LocalDate.now());
        newSubscription.setNextBillingAt(LocalDate.now().plusDays(30));

        Subscription savedSubscription = subscriptionRepository.save(newSubscription);
        return CurrentSubscriptionResponse.fromEntity(savedSubscription);
    }

    @Override
    public CurrentSubscriptionResponse cancelSubscription(User adminUser) {
        Company adminCompany = companyService.getAdminCompany(adminUser);

        Subscription subscription = subscriptionRepository
                .findByCompanyIdAndStatusIn(adminCompany.getId(), ACTIVE_STATUSES)
                .orElseThrow(() -> new EntityNotFoundException("Nenhuma assinatura ativa encontrada para cancelar."));

        subscription.setStatus(SubscriptionStatus.CANCELED);
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return CurrentSubscriptionResponse.fromEntity(savedSubscription);
    }
}