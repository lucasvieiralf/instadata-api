package br.com.grape.accessmanager.dto.company;

import br.com.grape.accessmanager.entity.Company;
import br.com.grape.accessmanager.enums.CompanyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyDTO {

    /**
     * Resposta com os detalhes da empresa do usuário logado.
     */
    public record CompanyDetailsResponse(
        Integer id,
        String tradingName,
        String legalName,
        String taxId,
        CompanyStatus status
    ) {
        public static CompanyDetailsResponse fromEntity(Company company) {
            return new CompanyDetailsResponse(
                company.getId(),
                company.getTradingName(),
                company.getLegalName(),
                company.getTaxId(),
                company.getStatus()
            );
        }
    }

    /**
     * Request para atualizar os dados da empresa.
     */
    public record UpdateCompanyRequest(
        @NotBlank(message = "O nome fantasia não pode estar em branco")
        String tradingName,

        String legalName,
        
        @Size(max = 20, message = "O ID Fiscal (CNPJ/CPF) deve ter no máximo 20 caracteres")
        String taxId
    ) {}
}