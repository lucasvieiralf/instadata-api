package br.com.grape.accessmanager.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.config.security.AppUserDetails;
import br.com.grape.accessmanager.entity.CompanyMember;
import br.com.grape.accessmanager.entity.User;
import br.com.grape.accessmanager.repository.CompanyMemberRepository;
import br.com.grape.accessmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyMemberRepository companyMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        // 1. Busca todos os vínculos (membros) do usuário
        List<CompanyMember> memberships = companyMemberRepository.findByUserId(user.getId());

        // 2. Coletamos todas as AUTORIDADES (Papéis + Permissões)
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        if (memberships.isEmpty()) {
            // Lógica para o SUPER_ADMIN (que pode não ter vínculos)
            if ("superadmin@grape.com".equals(user.getEmail())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
            }
        } else {
            for (CompanyMember member : memberships) {
                // Adiciona o Papel (ROLE_COMPANY_ADMIN)
                String roleName = member.getRole().getRoleName();
                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }
                authorities.add(new SimpleGrantedAuthority(roleName));

                // Adiciona todas as Permissões
                Set<GrantedAuthority> permissions = member.getRole().getPermissions().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
                        .collect(Collectors.toSet());
                authorities.addAll(permissions);
            }
        }

        // 3. Retorna o AppUserDetails customizado
        return new AppUserDetails(user, authorities);
    }
}