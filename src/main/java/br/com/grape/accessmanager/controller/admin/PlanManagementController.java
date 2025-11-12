package br.com.grape.accessmanager.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.grape.accessmanager.dto.admin.PlanManagementDTOs.AddLimitRequest;
import br.com.grape.accessmanager.dto.admin.PlanManagementDTOs.CreatePlanRequest;
import br.com.grape.accessmanager.dto.admin.PlanManagementDTOs.PlanResponse;
import br.com.grape.accessmanager.service.PlanManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/superadmin/plans")
@PreAuthorize("hasRole('SUPER_ADMIN')") // Proteção total
@RequiredArgsConstructor
public class PlanManagementController {
    
    private final PlanManagementService planManagementService;

    /**
     * (SUPER_ADMIN) Cria um novo Plano.
     */
    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(
            @Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.status(201).body(planManagementService.createPlan(request));
    }

    /**
     * (SUPER_ADMIN) Lista todos os Planos.
     */
    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        return ResponseEntity.ok(planManagementService.getAllPlans());
    }

    /**
     * (SUPER_ADMIN) Busca um Plano específico pelo ID.
     */
    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Integer planId) {
        return ResponseEntity.ok(planManagementService.getPlanById(planId));
    }

    /**
     * (SUPER_ADMIN) Adiciona um limite (Feature + Valor) a um Plano.
     */
    @PostMapping("/{planId}/limits")
    public ResponseEntity<PlanResponse> addLimitToPlan(
            @PathVariable Integer planId,
            @Valid @RequestBody AddLimitRequest request) {
        return ResponseEntity.ok(planManagementService.addLimitToPlan(planId, request));
    }

    /**
     * (SUPER_ADMIN) Remove um limite de um Plano.
     */
    @DeleteMapping("/{planId}/limits/{limitId}")
    public ResponseEntity<PlanResponse> removeLimitFromPlan(
            @PathVariable Integer planId,
            @PathVariable Integer limitId) {
        return ResponseEntity.ok(planManagementService.removeLimitFromPlan(planId, limitId));
    }
}