package com.example.palitrum.controller;

import com.example.palitrum.dto.RolePermissionDTO;
import com.example.palitrum.dto.RolePermissionResponse;
import com.example.palitrum.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService service;

    @GetMapping
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<List<RolePermissionResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission.assign_to_role')")
    public ResponseEntity<RolePermissionResponse> assign(@Valid @RequestBody RolePermissionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.assign(dto));
    }

    @DeleteMapping("/{roleId}/{permissionId}")
    @PreAuthorize("hasAuthority('permission.assign_to_role')")
    public ResponseEntity<Void> unassign(@PathVariable Long roleId, @PathVariable Long permissionId) {
        service.unassign(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }
}