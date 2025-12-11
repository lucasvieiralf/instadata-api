package br.com.instadata.accessmanager.service;

import br.com.instadata.accessmanager.dto.auth.LoginRequestDTO;
import br.com.instadata.accessmanager.dto.auth.LoginResponseDTO;
import br.com.instadata.accessmanager.dto.auth.RegisterRequestDTO;
import br.com.instadata.accessmanager.entity.User;

public interface AuthService {
    User registerCompanyAndAdmin(RegisterRequestDTO request);
    
    LoginResponseDTO login(LoginRequestDTO request);
}