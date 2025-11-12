package br.com.grape.accessmanager.service.impl;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.entity.ActivityLog;
import br.com.grape.accessmanager.entity.Company;
import br.com.grape.accessmanager.entity.User;
import br.com.grape.accessmanager.events.LoggableActivityEvent;
import br.com.grape.accessmanager.repository.ActivityLogRepository;
import br.com.grape.accessmanager.repository.CompanyRepository;
import br.com.grape.accessmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository logRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Async
    @EventListener(LoggableActivityEvent.class)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleActivityLogEvent(LoggableActivityEvent event) {
        try {
            // Busca as referências DENTRO da nova transação
            User user = event.getUserId() != null
                    ? userRepository.findById(event.getUserId()).orElse(null)
                    : null;

            Company company = event.getCompanyId() != null
                    ? companyRepository.findById(event.getCompanyId()).orElse(null)
                    : null;

            ActivityLog logEntry = new ActivityLog();
            logEntry.setActionKey(event.getActionKey());
            logEntry.setDescription(event.getDescription());
            logEntry.setUser(user);
            logEntry.setCompany(company);
            logEntry.setIpAddress(event.getIpAddress());

            logRepository.save(logEntry);

            log.debug("Log de atividade salvo: {}", event.getActionKey());

        } catch (Exception e) {
            log.error("FALHA AO GRAVAR LOG DE ATIVIDADE! Chave: {}. Erro: {}",
                    event.getActionKey(), e.getMessage(), e); // Loga a exceção inteira
        }
    }
}