package com.example.palitrum.controller;

import com.example.palitrum.dto.UserRoleDTO;
import com.example.palitrum.dto.UserRoleResponse;
import com.example.palitrum.service.UserRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private final UserRoleService service;

    public UserRoleController(UserRoleService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<List<UserRoleResponse>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long roleId
    ) {
        return ResponseEntity.ok(service.getByUserOrRole(userId, roleId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role.assign')")
    public ResponseEntity<UserRoleResponse> create(@RequestBody UserRoleDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role.revoke')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}