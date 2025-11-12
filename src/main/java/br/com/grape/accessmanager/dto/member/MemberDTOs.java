package br.com.grape.accessmanager.dto.member;

import br.com.grape.accessmanager.entity.CompanyMember;
import br.com.grape.accessmanager.enums.CompanyMemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTOs para o gerenciamento de Membros (CompanyMember)
 */
public class MemberDTOs {

    /**
     * Resposta ao listar os membros de uma empresa.
     */
    public record MemberResponse(
        Integer memberId,
        Integer userId,
        String userName,
        String userEmail,
        String roleName,
        CompanyMemberStatus status
    ) {
        public static MemberResponse fromEntity(CompanyMember member) {
            return new MemberResponse(
                member.getId(),
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole().getRoleName(),
                member.getStatus()
            );
        }
    }

    /**
     * Request para convidar (adicionar) um novo membro à empresa.
     */
    public record InviteMemberRequest(
        @NotBlank @Email
        String email,

        @NotBlank
        String name,

        @NotBlank
        String roleName // Ex: "COMPANY_ADMIN" ou "COMPANY_MEMBER"
    ) {}

    /**
     * Request para alterar o papel (Role) de um membro existente.
     */
    public record UpdateMemberRoleRequest(
        @NotBlank
        String roleName // Ex: "COMPANY_MEMBER"
    ) {}
}