package br.com.grape.accessmanager.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.grape.accessmanager.dto.admin.FeatureManagementDTOs.CreateFeatureRequest;
import br.com.grape.accessmanager.dto.admin.FeatureManagementDTOs.FeatureResponse;
import br.com.grape.accessmanager.service.PlanManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
// Novo sub-path para APIs de admin
@RequestMapping("/superadmin/features")
@PreAuthorize("hasRole('SUPER_ADMIN')") // Proteção total do controller
@RequiredArgsConstructor
public class FeatureManagementController {

    private final PlanManagementService planManagementService;

    /**
     * (SUPER_ADMIN) Cria uma nova "Feature" (funcionalidade) no sistema.
     * Ex: "MAX_USERS", "ALLOW_REPORTS", etc.
     */
    @PostMapping
    public ResponseEntity<FeatureResponse> createFeature(
            @Valid @RequestBody CreateFeatureRequest request) {
        return ResponseEntity.status(201).body(planManagementService.createFeature(request));
    }

    /**
     * (SUPER_ADMIN) Lista todas as "Features" cadastradas.
     */
    @GetMapping
    public ResponseEntity<List<FeatureResponse>> getAllFeatures() {
        return ResponseEntity.ok(planManagementService.getAllFeatures());
    }
}