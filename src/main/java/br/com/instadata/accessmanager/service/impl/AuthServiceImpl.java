package br.com.instadata.accessmanager.service.impl;

import org.springframework.security.authentication.AuthenticationManager; // NOVO
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // NOVO
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.instadata.accessmanager.dto.auth.LoginRequestDTO;
import br.com.instadata.accessmanager.dto.auth.LoginResponseDTO;
import br.com.instadata.accessmanager.dto.auth.RegisterRequestDTO;
import br.com.instadata.accessmanager.entity.Role;
import br.com.instadata.accessmanager.entity.User;
import br.com.instadata.accessmanager.enums.CompanyMemberStatus;
import br.com.instadata.accessmanager.enums.CompanyStatus;
import br.com.instadata.accessmanager.enums.UserStatus;
import br.com.instadata.accessmanager.repository.RoleRepository;
import br.com.instadata.accessmanager.repository.UserRepository;
import br.com.instadata.accessmanager.service.AuthService;
import br.com.instadata.accessmanager.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Injetando todos os componentes que precisamos
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional // Garante que ou tudo funciona, ou nada é salvo (rollback)
    public User registerCompanyAndAdmin(RegisterRequestDTO request) {
        
        // 1. Validar se o email já existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            // Criar exceções customizadas (ex: EmailAlreadyExistsException)
            throw new RuntimeException("Email já cadastrado");
        }

        // 2. Buscar o papel de "ADMIN"
        Role adminRole = roleRepository.findByRoleName("COMPANY_ADMIN")
                .orElseThrow(() -> new RuntimeException("Erro: Papel 'COMPANY_ADMIN' não encontrado."));

        // 3. Criar e salvar o novo Usuário
        User newUser = new User();
        newUser.setName(request.getUserName());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(UserStatus.ACTIVE); // Admin já começa ativo
        User savedUser = userRepository.save(newUser);

        return savedUser;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // 2. Se chegou aqui, o usuário está autenticado.
        // Carregar os detalhes dele (para pegar o email/username correto)
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // 3. Gerar o token JWT
        String jwtToken = jwtService.generateToken(userDetails);
        
        return LoginResponseDTO.builder()
                .token(jwtToken)
                .expiresIn(86400) // 24h em segundos
                .build();
    }
}