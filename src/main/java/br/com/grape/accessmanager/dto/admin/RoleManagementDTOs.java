package br.com.grape.accessmanager.dto.admin;

import java.util.Set;
import java.util.stream.Collectors;

import br.com.grape.accessmanager.entity.Permission;
import br.com.grape.accessmanager.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs para o gerenciamento de Papéis e Permissões
 */
public class RoleManagementDTOs {

    /** DTO para criar uma nova Permissão */
    public record CreatePermissionRequest(
        @NotBlank @Size(min = 3, max = 100)
        String permissionName,
        
        @Size(max = 255)
        String description
    ) {}

    /** DTO para criar um novo Papel (Role) */
    public record CreateRoleRequest(
        @NotBlank @Size(min = 3, max = 50)
        String roleName
    ) {}

    /** DTO simples para retornar dados de Permissão */
    public record PermissionResponse(
        Integer id,
        String permissionName,
        String description
    ) {
        public static PermissionResponse fromEntity(Permission p) {
            return new PermissionResponse(p.getId(), p.getPermissionName(), p.getDescription());
        }
    }

    /** DTO completo para retornar um Papel (Role) com suas permissões */
    public record RoleResponse(
        Integer id,
        String roleName,
        Set<PermissionResponse> permissions
    ) {
        public static RoleResponse fromEntity(Role role) {
            return new RoleResponse(
                role.getId(),
                role.getRoleName(),
                role.getPermissions().stream()
                    .map(PermissionResponse::fromEntity)
                    .collect(Collectors.toSet())
            );
        }
    }
}