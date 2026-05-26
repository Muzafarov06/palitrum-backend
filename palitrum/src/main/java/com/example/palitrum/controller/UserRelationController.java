package com.example.palitrum.controller;

import com.example.palitrum.dto.UserRelationDTO;
import com.example.palitrum.dto.UserResponseDto;
import com.example.palitrum.service.UserRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-relations")
public class UserRelationController {

    private final UserRelationService service;

    public UserRelationController(UserRelationService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user.view')")
    public ResponseEntity<List<UserRelationDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAuthority('user.view')")
    public ResponseEntity<List<UserRelationDTO>> getVerifiedByParent(@PathVariable Long parentId) {
        return ResponseEntity.ok(service.getVerifiedByParent(parentId));
    }

    // НОВЫЙ ЭНДПОИНТ: получить детей по ID родителя
    @GetMapping("/parent/{parentId}/children")
    @PreAuthorize("hasAnyRole('PARENT') or hasAuthority('user.view')")
    public ResponseEntity<List<UserResponseDto>> getChildrenByParent(@PathVariable Long parentId) {
        return ResponseEntity.ok(service.getChildrenByParent(parentId));
    }

    // НОВЫЙ ЭНДПОИНТ: получить родителей по ID ребёнка
    @GetMapping("/child/{childId}/parents")
    @PreAuthorize("hasAuthority('user.view')")
    public ResponseEntity<List<UserResponseDto>> getParentsByChild(@PathVariable Long childId) {
        return ResponseEntity.ok(service.getParentsByChild(childId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserRelationDTO> create(@RequestBody UserRelationDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}