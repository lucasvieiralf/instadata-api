package br.com.instadata.accessmanager.controller.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.instadata.accessmanager.dto.auth.LoginRequestDTO;
import br.com.instadata.accessmanager.dto.auth.LoginResponseDTO;
import br.com.instadata.accessmanager.dto.auth.RegisterRequestDTO;
import br.com.instadata.accessmanager.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCompanyAndAdmin(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            authService.registerCompanyAndAdmin(request);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Cadastro realizado com sucesso!"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou senha inválidos."));
        }
    }
}