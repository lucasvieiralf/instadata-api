package br.com.grape.accessmanager.service;

import java.util.List;

import br.com.grape.accessmanager.dto.admin.RoleManagementDTOs.*;

public interface RoleManagementService {

    PermissionResponse createPermission(CreatePermissionRequest request);
    List<PermissionResponse> getAllPermissions();
    void deletePermission(String permissionName);

    RoleResponse createRole(CreateRoleRequest request);
    List<RoleResponse> getAllRoles();

    RoleResponse getRoleByName(String roleName);
    RoleResponse addPermissionToRole(String roleName, String permissionName);
    RoleResponse removePermissionFromRole(String roleName, String permissionName);
}