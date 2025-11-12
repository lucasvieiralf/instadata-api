package br.com.grape.accessmanager.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.grape.accessmanager.dto.plan.PlanResponseDTO;
import br.com.grape.accessmanager.entity.Plan;
import br.com.grape.accessmanager.enums.PlanStatus;
import br.com.grape.accessmanager.repository.PlanRepository;
import br.com.grape.accessmanager.service.PlanService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public List<PlanResponseDTO> getAllActivePlans() {
        // 1. Busca no banco apenas os planos "ACTIVE"
        List<Plan> activePlans = planRepository.findByStatus(PlanStatus.ACTIVE);

        // 2. Converte a lista de Entidades para uma lista de DTOs
        return activePlans.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PlanResponseDTO convertToDTO(Plan plan) {
        PlanResponseDTO dto = new PlanResponseDTO();
        dto.setId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setMonthlyPrice(plan.getMonthlyPrice());
        return dto;
    }
}