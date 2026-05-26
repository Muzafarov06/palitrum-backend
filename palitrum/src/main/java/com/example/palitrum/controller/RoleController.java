package com.example.palitrum.controller;

import com.example.palitrum.dto.RoleCreateDto;
import com.example.palitrum.dto.RoleResponseDto;
import com.example.palitrum.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<Page<RoleResponseDto>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(roleService.getAll(search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role.view')")
    public ResponseEntity<RoleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role.create')")
    public ResponseEntity<RoleResponseDto> create(@Valid @RequestBody RoleCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role.update')")
    public ResponseEntity<RoleResponseDto> update(@PathVariable Long id, @Valid @RequestBody RoleCreateDto dto) {
        return ResponseEntity.ok(roleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}