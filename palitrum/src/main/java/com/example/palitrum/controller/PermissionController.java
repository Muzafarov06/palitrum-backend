package com.example.palitrum.controller;

import com.example.palitrum.dto.PermissionResponseDTO;
import com.example.palitrum.service.PermissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('permission.view')")
    public ResponseEntity<List<PermissionResponseDTO>> getAll() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission.view')")
    public ResponseEntity<PermissionResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission.assign_to_role')")
    public ResponseEntity<PermissionResponseDTO> create(@Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(permissionService.createPermission(request.code(), request.description()));
    }

    public record PermissionRequest(@NotBlank String code, String description) {}
}