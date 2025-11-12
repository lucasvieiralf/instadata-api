package br.com.grape.accessmanager.service;

import java.util.List;

import br.com.grape.accessmanager.dto.plan.PlanResponseDTO;

public interface PlanService {
    List<PlanResponseDTO> getAllActivePlans();
}