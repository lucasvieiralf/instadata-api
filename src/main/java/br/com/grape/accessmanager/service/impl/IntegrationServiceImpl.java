package br.com.grape.accessmanager.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.dto.integration.IntegrationDTOs.GenerateTokenRequest;
import br.com.grape.accessmanager.dto.integration.IntegrationDTOs.GeneratedTokenResponse;
import br.com.grape.accessmanager.dto.integration.IntegrationDTOs.TokenMetadataResponse;
import br.com.grape.accessmanager.entity.Company;
import br.com.grape.accessmanager.entity.CompanyMember;
import br.com.grape.accessmanager.entity.MemberPlatformAccess;
import br.com.grape.accessmanager.entity.Plan;
import br.com.grape.accessmanager.entity.PlanLimit;
import br.com.grape.accessmanager.entity.Platform;
import br.com.grape.accessmanager.entity.Subscription;
import br.com.grape.accessmanager.entity.User;
import br.com.grape.accessmanager.enums.PlatformAccessStatus;
import br.com.grape.accessmanager.enums.SubscriptionStatus;
import br.com.grape.accessmanager.events.LoggableActivityEvent;
import br.com.grape.accessmanager.repository.CompanyMemberRepository;
import br.com.grape.accessmanager.repository.MemberPlatformAccessRepository;
import br.com.grape.accessmanager.repository.PlatformRepository;
import br.com.grape.accessmanager.repository.SubscriptionRepository;
import br.com.grape.accessmanager.service.CompanyService;
import br.com.grape.accessmanager.service.IntegrationService;
import br.com.grape.accessmanager.service.TokenGenerationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class IntegrationServiceImpl implements IntegrationService {

        private final MemberPlatformAccessRepository accessRepository;
        private final CompanyMemberRepository memberRepository;
        private final PlatformRepository platformRepository;
        private final SubscriptionRepository subscriptionRepository;
        private final CompanyService companyService;
        private final TokenGenerationService tokenService;
        private final ApplicationEventPublisher eventPublisher;

        @Override
        public GeneratedTokenResponse generateTokenForMember(
                        GenerateTokenRequest request,
                        User adminUser,
                        String ipAddress) { // <-- IP RECEBIDO

                // 1. Validar a Empresa e a Assinatura
                Company adminCompany = companyService.getAdminCompany(adminUser);
                Subscription subscription = subscriptionRepository.findByCompanyIdAndStatusIn(
                                adminCompany.getId(), List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING))
                                .orElseThrow(() -> new RuntimeException("Nenhuma assinatura ativa encontrada."));

                // 2. Validar Membro e Plataforma
                CompanyMember member = memberRepository.findById(request.memberId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Membro não encontrado com ID: " + request.memberId()));

                Platform platform = platformRepository.findById(request.platformId())
                                .orElseThrow(
                                                () -> new EntityNotFoundException("Plataforma não encontrada com ID: "
                                                                + request.platformId()));

                // 3. Validação de Segurança (Garantir que o membro pertence à empresa do admin)
                if (!member.getCompany().getId().equals(adminCompany.getId())) {
                        throw new SecurityException("Acesso negado: O membro não pertence à sua empresa.");
                }

                // 4. Verificar se o registro (4, 1) já existe
                Optional<MemberPlatformAccess> existingAccessOpt = accessRepository
                                .findByCompanyMemberIdAndPlatformId(member.getId(), platform.getId());

                MemberPlatformAccess accessToSave;
                String logActionKey;

                if (existingAccessOpt.isPresent()) {
                        
                        MemberPlatformAccess existingAccess = existingAccessOpt.get();

                        if (existingAccess.getStatus() == PlatformAccessStatus.ACTIVE) {
                                // Se está ATIVO, bloqueia.
                                throw new ResponseStatusException(HttpStatus.CONFLICT, // 409 Conflict
                                                "Um token de acesso ativo já existe para este membro e plataforma. " +
                                                                "Revogue o token antigo antes de gerar um novo.");
                        } else {
                                // Se está REVOKED, vamos REATIVÁ-LO.
                                accessToSave = existingAccess;
                                accessToSave.setStatus(PlatformAccessStatus.ACTIVE);
                                logActionKey = "TOKEN_REGENERATED"; // Chave de log diferente
                        }

                } else {
                        // 5. Validar Limites do Plano (SÓ para tokens novos)
                        Plan plan = subscription.getPlan();

                        // Conta apenas tokens ATIVOS
                        int activeTokenCount = accessRepository.countByCompanyMember_Company_IdAndPlatform_IdAndStatus(
                                        adminCompany.getId(),
                                        platform.getId(),
                                        PlatformAccessStatus.ACTIVE); // <-- O BUG DO LIMITE, CORRIGIDO!

                        int maxTokens = getLimitFromPlan(plan, "MAX_USERS");

                        if (activeTokenCount >= maxTokens) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                                String.format("Limite de tokens ativos (%d) para o seu plano atingido.",
                                                                maxTokens));
                        }

                        // Limite OK, criar o novo registro
                        accessToSave = new MemberPlatformAccess();
                        accessToSave.setCompanyMember(member);
                        accessToSave.setPlatform(platform);
                        accessToSave.setStatus(PlatformAccessStatus.ACTIVE);
                        logActionKey = "TOKEN_GENERATED"; // Chave de log de criação
                }

                // 6. Gerar e Salvar o Token
                // Gerar um novo token (hash) tanto para o NOVO quanto para o REATIVADO
                TokenGenerationService.GeneratedToken tokenPair = tokenService.generateApiToken();
                accessToSave.setAccessToken(tokenPair.hashedToken()); // Salva o HASH
                MemberPlatformAccess savedAccess = accessRepository.save(accessToSave);

                // 7. DISPARAR O EVENTO (COM IP)
                LoggableActivityEvent logEvent = new LoggableActivityEvent(
                                logActionKey,
                                String.format("Token (%s) para o membro '%s' na plataforma '%s'.",
                                logActionKey.toLowerCase(),
                                member.getUser().getName(), platform.getPlatformName()),
                                logActionKey,
                                adminUser.getId(),
                                adminCompany.getId(),
                                ipAddress);
                eventPublisher.publishEvent(logEvent);

                // 8. Retorna o DTO com o token em texto puro (só desta vez)
                return GeneratedTokenResponse.fromEntity(savedAccess, tokenPair.plainTextToken());
        }

        @Override
        public TokenMetadataResponse revokeToken(
                        Integer accessId,
                        User adminUser,
                        String ipAddress) {

                Company adminCompany = companyService.getAdminCompany(adminUser);

                MemberPlatformAccess access = accessRepository.findById(accessId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Token (Access) não encontrado com ID: " + accessId));

                // Validação de Segurança
                if (!access.getCompanyMember().getCompany().getId().equals(adminCompany.getId())) {
                        throw new SecurityException(
                                        "Acesso negado: Você não pode revogar um token que não pertence à sua empresa.");
                }

                access.setStatus(PlatformAccessStatus.REVOKED);
                MemberPlatformAccess savedAccess = accessRepository.save(access);

                // DISPARAR O EVENTO (COM IP)
                String logDescription = String.format(
                                "Admin '%s' (ID: %d) revogou o token (ID: %d) do membro '%s' (ID: %d)",
                                adminUser.getName(),
                                adminUser.getId(),
                                access.getId(),
                                access.getCompanyMember().getUser().getName(),
                                access.getCompanyMember().getId());

                LoggableActivityEvent logEvent = new LoggableActivityEvent(
                                logDescription,
                                "MEMBER_TOKEN_REVOKED",
                                logDescription,
                                adminUser.getId(),
                                adminCompany.getId(),
                                ipAddress);
                eventPublisher.publishEvent(logEvent);

                return TokenMetadataResponse.fromEntity(savedAccess);
        }

        @Override
        @Transactional(readOnly = true)
        public List<TokenMetadataResponse> listCompanyTokens(User adminUser) {
                Company adminCompany = companyService.getAdminCompany(adminUser);
                return accessRepository.findByCompanyMember_Company_Id(adminCompany.getId()).stream()
                                .map(TokenMetadataResponse::fromEntity)
                                .collect(Collectors.toList());
        }

        /**
         * Busca um limite específico (pela featureKey) dentro de um plano.
         */
        private int getLimitFromPlan(Plan plan, String featureKey) {
                String limitValue = plan.getPlanLimits().stream()
                                .filter(limit -> limit.getFeature().getFeatureKey().equals(featureKey))
                                .findFirst()
                                .map(PlanLimit::getLimitValue)
                                .orElse("0"); // Default "0" se a feature não estiver no plano

                try {
                        return Integer.parseInt(limitValue);
                } catch (NumberFormatException e) {
                        return 0; // Default "0" se o valor for inválido (ex: "true")
                }
        }
}