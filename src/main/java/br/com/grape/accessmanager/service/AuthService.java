package br.com.grape.accessmanager.service;

import br.com.grape.accessmanager.dto.auth.LoginRequestDTO;
import br.com.grape.accessmanager.dto.auth.LoginResponseDTO;
import br.com.grape.accessmanager.dto.auth.RegisterRequestDTO;
import br.com.grape.accessmanager.entity.User;

public interface AuthService {
    User registerCompanyAndAdmin(RegisterRequestDTO request);
    
    LoginResponseDTO login(LoginRequestDTO request);
}