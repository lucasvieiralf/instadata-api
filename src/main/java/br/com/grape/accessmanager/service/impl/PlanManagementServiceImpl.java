package br.com.grape.accessmanager.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.dto.admin.FeatureManagementDTOs.CreateFeatureRequest;
import br.com.grape.accessmanager.dto.admin.FeatureManagementDTOs.FeatureResponse;
import br.com.grape.accessmanager.dto.admin.PlanManagementDTOs.AddLimitRequest;
import br.com.grape.accessmanager.dto.admin.PlanManagementDTOs.CreatePlanRequest;
import br.com.grape.accessmanager.dto.admin.PlanManagementDTOs.PlanResponse;
import br.com.grape.accessmanager.entity.Feature;
import br.com.grape.accessmanager.entity.Plan;
import br.com.grape.accessmanager.entity.PlanLimit;
import br.com.grape.accessmanager.repository.FeatureRepository;
import br.com.grape.accessmanager.repository.PlanLimitRepository;
import br.com.grape.accessmanager.repository.PlanRepository;
import br.com.grape.accessmanager.service.PlanManagementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanManagementServiceImpl implements PlanManagementService {

    private final FeatureRepository featureRepository;
    private final PlanRepository planRepository;
    private final PlanLimitRepository planLimitRepository;

    @Override
    public FeatureResponse createFeature(CreateFeatureRequest request) {
        if (featureRepository.findByFeatureKey(request.featureKey()).isPresent()) {
            throw new RuntimeException("Feature com a key '" + request.featureKey() + "' já existe.");
        }
        Feature feature = new Feature();
        feature.setFeatureKey(request.featureKey());
        feature.setDisplayName(request.displayName());
        feature.setDataType(request.dataType());
        
        Feature savedFeature = featureRepository.save(feature);
        return FeatureResponse.fromEntity(savedFeature);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeatureResponse> getAllFeatures() {
        return featureRepository.findAll().stream()
                .map(FeatureResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PlanResponse createPlan(CreatePlanRequest request) {
        if (planRepository.findByPlanName(request.planName()).isPresent()) {
            throw new RuntimeException("Plano com o nome '" + request.planName() + "' já existe.");
        }
        Plan plan = new Plan();
        plan.setPlanName(request.planName());
        plan.setMonthlyPrice(request.monthlyPrice());
        plan.setStatus(request.status());

        Plan savedPlan = planRepository.save(plan);
        return PlanResponse.fromEntity(savedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(PlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse getPlanById(Integer planId) {
        return planRepository.findById(planId)
                .map(PlanResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado com ID: " + planId));
    }

    @Override
    public PlanResponse addLimitToPlan(Integer planId, AddLimitRequest request) {
        // 1. Acha o Plano
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado com ID: " + planId));
        
        // 2. Acha a Feature
        Feature feature = featureRepository.findByFeatureKey(request.featureKey())
                .orElseThrow(() -> new EntityNotFoundException("Feature não encontrada com Key: " + request.featureKey()));

        // 3. Verifica se o limite já existe
        planLimitRepository.findByPlanAndFeature(plan, feature).ifPresent(limit -> {
            throw new RuntimeException("Este plano já possui um limite para a feature: " + request.featureKey());
        });

        // 4. Cria e salva o novo limite
        PlanLimit newLimit = new PlanLimit();
        newLimit.setPlan(plan);
        newLimit.setFeature(feature);
        newLimit.setLimitValue(request.limitValue());
        planLimitRepository.save(newLimit);

        // 5. Retorna o plano atualizado (o .save() acima atualiza a transação)
        // Recarregamos a entidade para garantir que a lista 'planLimits' esteja atualizada
        Plan updatedPlan = planRepository.findById(planId).get();
        return PlanResponse.fromEntity(updatedPlan);
    }

    @Override
    public PlanResponse removeLimitFromPlan(Integer planId, Integer limitId) {
        // 1. Acha o Limite
        PlanLimit limit = planLimitRepository.findById(limitId)
                .orElseThrow(() -> new EntityNotFoundException("Limite não encontrado com ID: " + limitId));

        // 2. Validação de segurança
        if (!limit.getPlan().getId().equals(planId)) {
            throw new RuntimeException("Erro de associação: O limite " + limitId + " não pertence ao plano " + planId);
        }

        // 3. Remove o limite
        planLimitRepository.delete(limit);

        // 4. Retorna o plano atualizado
        Plan updatedPlan = planRepository.findById(planId).get();
        return PlanResponse.fromEntity(updatedPlan);
    }
}