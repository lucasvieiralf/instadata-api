package br.com.grape.accessmanager.service.impl;

import org.springframework.security.authentication.AuthenticationManager; // NOVO
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // NOVO
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.dto.auth.LoginRequestDTO;
import br.com.grape.accessmanager.dto.auth.LoginResponseDTO;
import br.com.grape.accessmanager.dto.auth.RegisterRequestDTO;
import br.com.grape.accessmanager.entity.Company;
import br.com.grape.accessmanager.entity.CompanyMember;
import br.com.grape.accessmanager.entity.Role;
import br.com.grape.accessmanager.entity.User;
import br.com.grape.accessmanager.enums.CompanyMemberStatus;
import br.com.grape.accessmanager.enums.CompanyStatus;
import br.com.grape.accessmanager.enums.UserStatus;
import br.com.grape.accessmanager.repository.CompanyMemberRepository;
import br.com.grape.accessmanager.repository.CompanyRepository;
import br.com.grape.accessmanager.repository.RoleRepository;
import br.com.grape.accessmanager.repository.UserRepository;
import br.com.grape.accessmanager.service.AuthService;
import br.com.grape.accessmanager.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Injetando todos os componentes que precisamos
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final CompanyMemberRepository companyMemberRepository;
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

        // 2. Buscar o papel de "ADMIN" (supomos que ele exista no banco)
        Role adminRole = roleRepository.findByRoleName("COMPANY_ADMIN")
                .orElseThrow(() -> new RuntimeException("Erro: Papel 'COMPANY_ADMIN' não encontrado."));

        // 3. Criar e salvar o novo Usuário
        User newUser = new User();
        newUser.setName(request.getUserName());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setStatus(UserStatus.ACTIVE); // Admin já começa ativo
        User savedUser = userRepository.save(newUser);

        // 4. Criar e salvar a nova Empresa (em modo TRIAL)
        Company newCompany = new Company();
        newCompany.setTradingName(request.getCompanyName());
        newCompany.setStatus(CompanyStatus.TRIAL); // Bom default para SaaS
        Company savedCompany = companyRepository.save(newCompany);

        // 5. Vincular o Usuário à Empresa com o Papel de Admin
        CompanyMember newMember = new CompanyMember();
        newMember.setUser(savedUser);
        newMember.setCompany(savedCompany);
        newMember.setRole(adminRole);
        newMember.setStatus(CompanyMemberStatus.ACTIVE);
        companyMemberRepository.save(newMember);

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