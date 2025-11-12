package br.com.grape.accessmanager.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.grape.accessmanager.dto.admin.RoleManagementDTOs.CreatePermissionRequest;
import br.com.grape.accessmanager.dto.admin.RoleManagementDTOs.CreateRoleRequest;
import br.com.grape.accessmanager.dto.admin.RoleManagementDTOs.PermissionResponse;
import br.com.grape.accessmanager.dto.admin.RoleManagementDTOs.RoleResponse;
import br.com.grape.accessmanager.entity.Permission;
import br.com.grape.accessmanager.entity.Role;
import br.com.grape.accessmanager.repository.PermissionRepository;
import br.com.grape.accessmanager.repository.RoleRepository;
import br.com.grape.accessmanager.service.RoleManagementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleManagementServiceImpl implements RoleManagementService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionRepository.findByPermissionName(request.permissionName()).isPresent()) {
            throw new RuntimeException("Permissão '" + request.permissionName() + "' já existe.");
        }
        Permission newPermission = new Permission(request.permissionName(), request.description());
        Permission saved = permissionRepository.save(newPermission);
        return PermissionResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePermission(String permissionName) {
        Permission permission = findPermissionOrThrow(permissionName);

        permissionRepository.delete(permission);
    }

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.findByRoleName(request.roleName()).isPresent()) {
            throw new RuntimeException("Papel '" + request.roleName() + "' já existe.");
        }
        Role newRole = new Role();
        newRole.setRoleName(request.roleName());
        Role saved = roleRepository.save(newRole);
        return RoleResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String roleName) {
        return RoleResponse.fromEntity(findRoleOrThrow(roleName));
    }

    @Override
    public RoleResponse addPermissionToRole(String roleName, String permissionName) {
        Role role = findRoleOrThrow(roleName);
        Permission permission = findPermissionOrThrow(permissionName);

        role.getPermissions().add(permission);

        Role savedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(savedRole);
    }

    @Override
    public RoleResponse removePermissionFromRole(String roleName, String permissionName) {
        Role role = findRoleOrThrow(roleName);
        Permission permission = findPermissionOrThrow(permissionName);

        role.getPermissions().remove(permission);
        Role savedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(savedRole);
    }

    private Role findRoleOrThrow(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Papel não encontrado: " + roleName));
    }

    private Permission findPermissionOrThrow(String permissionName) {
        return permissionRepository.findByPermissionName(permissionName)
                .orElseThrow(() -> new EntityNotFoundException("Permissão não encontrada: " + permissionName));
    }
}