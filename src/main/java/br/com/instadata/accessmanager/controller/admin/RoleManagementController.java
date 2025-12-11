package br.com.instadata.accessmanager.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.instadata.accessmanager.dto.admin.RoleManagementDTOs.CreatePermissionRequest;
import br.com.instadata.accessmanager.dto.admin.RoleManagementDTOs.CreateRoleRequest;
import br.com.instadata.accessmanager.dto.admin.RoleManagementDTOs.PermissionResponse;
import br.com.instadata.accessmanager.dto.admin.RoleManagementDTOs.RoleResponse;
import br.com.instadata.accessmanager.service.RoleManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/superadmin/management")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Roles")
public class RoleManagementController {

    private final RoleManagementService roleManagementService;

    @PostMapping("/permissions")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        return ResponseEntity.status(201).body(roleManagementService.createPermission(request));
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(roleManagementService.getAllPermissions());
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(201).body(roleManagementService.createRole(request));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleManagementService.getAllRoles());
    }

    @GetMapping("/roles/{roleName}")
    public ResponseEntity<RoleResponse> getRoleByName(@PathVariable String roleName) {
        return ResponseEntity.ok(roleManagementService.getRoleByName(roleName));
    }

    @PostMapping("/roles/{roleName}/permissions/{permissionName}")
    public ResponseEntity<RoleResponse> addPermissionToRole(
            @PathVariable String roleName,
            @PathVariable String permissionName) {
        return ResponseEntity.ok(roleManagementService.addPermissionToRole(roleName, permissionName));
    }

    @DeleteMapping("/roles/{roleName}/permissions/{permissionName}")
    public ResponseEntity<RoleResponse> removePermissionFromRole(
            @PathVariable String roleName,
            @PathVariable String permissionName) {
        return ResponseEntity.ok(roleManagementService.removePermissionFromRole(roleName, permissionName));
    }
}