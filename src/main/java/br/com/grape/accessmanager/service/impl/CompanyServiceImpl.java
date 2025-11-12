package br.com.grape.accessmanager.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.dto.company.CompanyDTO.CompanyDetailsResponse;
import br.com.grape.accessmanager.dto.company.CompanyDTO.UpdateCompanyRequest;
import br.com.grape.accessmanager.entity.Company;
import br.com.grape.accessmanager.entity.CompanyMember;
import br.com.grape.accessmanager.entity.User;
import br.com.grape.accessmanager.repository.CompanyMemberRepository;
import br.com.grape.accessmanager.repository.CompanyRepository;
import br.com.grape.accessmanager.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyMemberRepository companyMemberRepository;
    private final CompanyRepository companyRepository; // Necessário para salvar

    /**
     * Busca a empresa principal do admin.
     * Lógica centralizada aqui.
     */
    @Override
    @Transactional(readOnly = true)
    public Company getAdminCompany(User adminUser) {
        return companyMemberRepository.findByUserId(adminUser.getId())
            .stream()
            .findFirst() // Pega o primeiro vínculo
            .map(CompanyMember::getCompany) // Mapeia para a Empresa
            .orElseThrow(() -> new EntityNotFoundException("Usuário admin não está associado a nenhuma empresa."));
    }

    /**
     * Busca os detalhes da empresa (para o DTO).
     */
    @Override
    @Transactional(readOnly = true)
    public CompanyDetailsResponse getMyCompany(User adminUser) {
        Company company = getAdminCompany(adminUser);
        return CompanyDetailsResponse.fromEntity(company);
    }

    /**
     * Atualiza os dados da empresa.
     */
    @Override
    public CompanyDetailsResponse updateMyCompany(User adminUser, UpdateCompanyRequest request) {
        Company company = getAdminCompany(adminUser);

        // Atualiza os campos permitidos
        company.setTradingName(request.tradingName());
        company.setLegalName(request.legalName());
        company.setTaxId(request.taxId());

        Company updatedCompany = companyRepository.save(company);
        return CompanyDetailsResponse.fromEntity(updatedCompany);
    }
}