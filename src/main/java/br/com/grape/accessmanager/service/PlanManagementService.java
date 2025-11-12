package br.com.grape.accessmanager.service;

import java.util.List;

import br.com.grape.accessmanager.dto.admin.FeatureManagementDTOs.*;
import br.com.grape.accessmanager.dto.admin.PlanManagementDTOs.*;

public interface PlanManagementService {
    FeatureResponse createFeature(CreateFeatureRequest request);
    List<FeatureResponse> getAllFeatures();

    List<PlanResponse> getAllPlans();
    PlanResponse createPlan(CreatePlanRequest request);
    PlanResponse getPlanById(Integer planId);

    PlanResponse addLimitToPlan(Integer planId, AddLimitRequest request);
    PlanResponse removeLimitFromPlan(Integer planId, Integer limitId);
}