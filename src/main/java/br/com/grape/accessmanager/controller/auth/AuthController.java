package br.com.grape.accessmanager.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.grape.accessmanager.dto.auth.LoginRequestDTO;
import br.com.grape.accessmanager.dto.auth.LoginResponseDTO;
import br.com.grape.accessmanager.dto.auth.RegisterRequestDTO;
import br.com.grape.accessmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCompanyAndAdmin(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            authService.registerCompanyAndAdmin(request);
            return ResponseEntity.ok().body("Cadastro realizado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Email ou senha inválidos.");
        }
    }
}