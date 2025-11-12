package br.com.grape.accessmanager.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.grape.accessmanager.dto.admin.PlatformManagementDTOs.CreatePlatformRequest;
import br.com.grape.accessmanager.dto.admin.PlatformManagementDTOs.PlatformResponse;
import br.com.grape.accessmanager.entity.Platform;
import br.com.grape.accessmanager.repository.PlatformRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/superadmin/platforms")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class PlatformManagementController {

    private final PlatformRepository platformRepository;

    /**
     * Cria uma nova plataforma (Ex: Nexos ERP)
     */
    @PostMapping
    public ResponseEntity<PlatformResponse> createPlatform(@Valid @RequestBody CreatePlatformRequest request) {
        Platform newPlatform = new Platform();
        newPlatform.setPlatformName(request.platformName());
        newPlatform.setPlatformKey(request.platformKey());
        newPlatform.setStatus(request.status());
        
        Platform savedPlatform = platformRepository.save(newPlatform);
        return ResponseEntity.status(HttpStatus.CREATED).body(PlatformResponse.fromEntity(savedPlatform));
    }

    /**
     * Lista todas as plataformas cadastradas no sistema.
     */
    @GetMapping
    public ResponseEntity<List<PlatformResponse>> getAllPlatforms() {
        List<PlatformResponse> platforms = platformRepository.findAll().stream()
            .map(PlatformResponse::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(platforms);
    }
}