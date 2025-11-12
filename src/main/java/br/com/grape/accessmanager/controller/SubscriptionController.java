package br.com.grape.accessmanager.controller;

import java.util.List;
import java.util.Optional; // <-- IMPORTADO

import org.springframework.http.HttpStatus; // <-- IMPORTADO
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.grape.accessmanager.config.security.AppUserDetails;
import br.com.grape.accessmanager.dto.subscription.SubscriptionDTO.*;
import br.com.grape.accessmanager.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PublicPlanResponse>> listAvailablePlans() {
        return ResponseEntity.ok(subscriptionService.listAvailablePlans());
    }

    /**
     * Busca a assinatura atual da empresa.
     * Se não houver, retorna 404 (Not Found).
     */
    @GetMapping("/my-subscription")
    @PreAuthorize("hasAuthority('MANAGE_BILLING')")
    public ResponseEntity<CurrentSubscriptionResponse> getMySubscription(
            @AuthenticationPrincipal AppUserDetails principal) {
        
        Optional<CurrentSubscriptionResponse> subscriptionOpt = subscriptionService.getMySubscription(principal.getUserEntity());
        
        return subscriptionOpt
            .map(ResponseEntity::ok) // Se "presente", retorna 200 OK com o body
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Se "vazio", retorna 404
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_BILLING')")
    public ResponseEntity<CurrentSubscriptionResponse> createSubscription(
            @AuthenticationPrincipal AppUserDetails principal,
            @Valid @RequestBody SubscribeRequest request) {
        
        CurrentSubscriptionResponse newSubscription = subscriptionService.createSubscription(request, principal.getUserEntity());
        return ResponseEntity.status(201).body(newSubscription);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('MANAGE_BILLING')")
    public ResponseEntity<CurrentSubscriptionResponse> cancelSubscription(
            @AuthenticationPrincipal AppUserDetails principal) {
        
        return ResponseEntity.ok(subscriptionService.cancelSubscription(principal.getUserEntity()));
    }
}