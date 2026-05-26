package com.example.palitrum.service;

import com.example.palitrum.dto.PermissionResponseDTO;
import com.example.palitrum.model.Permission;
import com.example.palitrum.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(p -> new PermissionResponseDTO(p.getId(), p.getCode(), p.getDescription()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PermissionResponseDTO getById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Разрешение не найдено: " + id));
        return new PermissionResponseDTO(permission.getId(), permission.getCode(), permission.getDescription());
    }

    @Transactional
    public PermissionResponseDTO createPermission(String code, String description) {
        String normalized = code.toUpperCase();
        if (permissionRepository.existsByCode(normalized)) {
            throw new RuntimeException("Разрешение с кодом '" + normalized + "' уже существует");
        }
        Permission permission = Permission.builder()
                .code(normalized)
                .description(description)
                .build();
        Permission saved = permissionRepository.save(permission);
        return new PermissionResponseDTO(saved.getId(), saved.getCode(), saved.getDescription());
    }
}