package br.com.grape.accessmanager.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.dto.member.MemberDTOs.InviteMemberRequest;
import br.com.grape.accessmanager.dto.member.MemberDTOs.MemberResponse;
import br.com.grape.accessmanager.dto.member.MemberDTOs.UpdateMemberRoleRequest;
import br.com.grape.accessmanager.entity.Company;
import br.com.grape.accessmanager.entity.CompanyMember;
import br.com.grape.accessmanager.entity.Role;
import br.com.grape.accessmanager.entity.User;
import br.com.grape.accessmanager.enums.CompanyMemberStatus;
import br.com.grape.accessmanager.enums.UserStatus;
import br.com.grape.accessmanager.repository.CompanyMemberRepository;
import br.com.grape.accessmanager.repository.RoleRepository;
import br.com.grape.accessmanager.repository.UserRepository;
import br.com.grape.accessmanager.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final CompanyMemberRepository companyMemberRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberResponse inviteMember(InviteMemberRequest request, User adminUser) {
        // 1. Identificar a Empresa do Admin
        Company adminCompany = getAdminCompany(adminUser);

        // 2. Buscar o Papel (Role)
        Role targetRole = roleRepository.findByRoleName(request.roleName())
                .orElseThrow(() -> new EntityNotFoundException("Papel '" + request.roleName() + "' não encontrado."));

        // 3. Buscar ou Criar o Usuário
        Optional<User> existingUserOpt = userRepository.findByEmail(request.email());

        User targetUser;
        if (existingUserOpt.isEmpty()) {
            // Usuário não existe, vamos criar um
            User newUser = new User();
            newUser.setEmail(request.email());
            newUser.setName(request.name());
            // Cria uma senha aleatória. O usuário usará o "Esqueci minha senha"
            String tempPassword = UUID.randomUUID().toString();
            newUser.setPasswordHash(passwordEncoder.encode(tempPassword));
            newUser.setStatus(UserStatus.ACTIVE); // Ou PENDING, se houver fluxo de convite
            targetUser = userRepository.save(newUser);
        } else {
            targetUser = existingUserOpt.get();
        }

        // 4. Verificar se o usuário já é membro desta empresa
        companyMemberRepository.findByUserIdAndCompanyId(targetUser.getId(), adminCompany.getId()).ifPresent(m -> {
            throw new RuntimeException("Usuário já é membro desta empresa.");
        });

        // 5. Verificar Limite de Usuários do Plano (ex: MAX_USERS)
        // Isso exigiria injetar o SubscriptionService e o PlanLimitRepository
        // Por enquanto, vamos pular.

        // 6. Criar o vínculo (CompanyMember)
        CompanyMember newMember = new CompanyMember();
        newMember.setUser(targetUser);
        newMember.setCompany(adminCompany);
        newMember.setRole(targetRole);
        newMember.setStatus(CompanyMemberStatus.ACTIVE); // Ou PENDING

        CompanyMember savedMember = companyMemberRepository.save(newMember);

        return MemberResponse.fromEntity(savedMember);
    }

    @Override
    public void removeMember(Integer memberId, User adminUser) {
        Company adminCompany = getAdminCompany(adminUser);
        CompanyMember member = findAndValidateMember(memberId, adminCompany.getId());

        // Verificação de segurança: não pode se remover
        if (member.getUser().getId().equals(adminUser.getId())) {
            throw new RuntimeException("Você não pode remover a si mesmo.");
        }

        companyMemberRepository.delete(member);
    }

    @Override
    public MemberResponse changeMemberRole(Integer memberId, UpdateMemberRoleRequest request, User adminUser) {
        Company adminCompany = getAdminCompany(adminUser);
        CompanyMember member = findAndValidateMember(memberId, adminCompany.getId());

        Role newRole = roleRepository.findByRoleName(request.roleName())
                .orElseThrow(() -> new EntityNotFoundException("Papel '" + request.roleName() + "' não encontrado."));

        member.setRole(newRole);
        CompanyMember updatedMember = companyMemberRepository.save(member);

        return MemberResponse.fromEntity(updatedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(User adminUser) {
        Company adminCompany = getAdminCompany(adminUser);

        return companyMemberRepository.findByCompanyId(adminCompany.getId()).stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private Company getAdminCompany(User adminUser) {
        // Usamos findByUserId, que retorna List<CompanyMember>
        return companyMemberRepository.findByUserId(adminUser.getId())
                .stream()
                .findFirst() // Pega o primeiro vínculo
                .map(CompanyMember::getCompany) // Mapeia para a Empresa
                .orElseThrow(() -> new EntityNotFoundException("Admin não está associado a nenhuma empresa."));
    }

    private CompanyMember findAndValidateMember(Integer memberId, Integer adminCompanyId) {
        CompanyMember member = companyMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Membro com ID " + memberId + " não encontrado."));

        // Validação de segurança crucial
        if (!member.getCompany().getId().equals(adminCompanyId)) {
            // Segurança: Não vazar informação.
            throw new EntityNotFoundException("Membro com ID " + memberId + " não encontrado.");
        }
        return member;
    }
}