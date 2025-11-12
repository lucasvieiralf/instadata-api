package br.com.grape.accessmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.grape.accessmanager.config.security.AppUserDetails;
import br.com.grape.accessmanager.dto.integration.IntegrationDTOs.GenerateTokenRequest;
import br.com.grape.accessmanager.dto.integration.IntegrationDTOs.GeneratedTokenResponse;
import br.com.grape.accessmanager.dto.integration.IntegrationDTOs.TokenMetadataResponse;
import br.com.grape.accessmanager.service.IntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/integrations")
@PreAuthorize("hasAuthority('MANAGE_COMPANY_SETTINGS')")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationService integrationService;

    @GetMapping
    public ResponseEntity<List<TokenMetadataResponse>> listMyTokens(
            @AuthenticationPrincipal AppUserDetails principal) {

        return ResponseEntity.ok(integrationService.listCompanyTokens(principal.getUserEntity()));
    }

    @PostMapping("/generate")
    public ResponseEntity<GeneratedTokenResponse> generateToken(
            @AuthenticationPrincipal AppUserDetails principal,
            @Valid @RequestBody GenerateTokenRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIp(httpRequest);

        GeneratedTokenResponse response = integrationService.generateTokenForMember(
                request,
                principal.getUserEntity(),
                ipAddress
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{accessId}")
    public ResponseEntity<TokenMetadataResponse> revokeToken(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Integer accessId,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIp(httpRequest);

        return ResponseEntity.ok(integrationService.revokeToken(
                accessId,
                principal.getUserEntity(),
                ipAddress
        ));
    }

    /**
     * Utilitário para pegar o IP real do cliente,
     * considerando proxies (X-Forwarded-For).
     */
    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}