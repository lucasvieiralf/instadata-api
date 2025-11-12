package br.com.grape.accessmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.grape.accessmanager.config.security.AppUserDetails;
import br.com.grape.accessmanager.dto.company.CompanyDTO.CompanyDetailsResponse;
import br.com.grape.accessmanager.dto.company.CompanyDTO.UpdateCompanyRequest;
import br.com.grape.accessmanager.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Busca os detalhes da empresa do usuário logado.
     * Aberto para qualquer usuário autenticado da empresa.
     */
    @GetMapping("/my-company")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompanyDetailsResponse> getMyCompany(
            @AuthenticationPrincipal AppUserDetails principal) {
        
        return ResponseEntity.ok(companyService.getMyCompany(principal.getUserEntity()));
    }

    /**
     * Atualiza os detalhes da empresa do usuário logado.
     * Protegido pela nova permissão 'MANAGE_COMPANY_SETTINGS'.
     */
    @PutMapping("/my-company")
    @PreAuthorize("hasAuthority('MANAGE_COMPANY_SETTINGS')")
    public ResponseEntity<CompanyDetailsResponse> updateMyCompany(
            @AuthenticationPrincipal AppUserDetails principal,
            @Valid @RequestBody UpdateCompanyRequest request) {
        
        CompanyDetailsResponse updatedCompany = companyService.updateMyCompany(principal.getUserEntity(), request);
        return ResponseEntity.ok(updatedCompany);
    }
}