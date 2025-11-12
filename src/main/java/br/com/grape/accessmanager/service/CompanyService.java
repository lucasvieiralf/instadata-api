package br.com.grape.accessmanager.service;

import br.com.grape.accessmanager.dto.company.CompanyDTO.*;
import br.com.grape.accessmanager.entity.Company;
import br.com.grape.accessmanager.entity.User;

public interface CompanyService {
    Company getAdminCompany(User adminUser);
    CompanyDetailsResponse getMyCompany(User adminUser);
    CompanyDetailsResponse updateMyCompany(User adminUser, UpdateCompanyRequest request);
}