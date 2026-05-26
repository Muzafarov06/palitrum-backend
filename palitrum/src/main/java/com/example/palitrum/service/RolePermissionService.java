package com.example.palitrum.service;

import com.example.palitrum.dto.RolePermissionDTO;
import com.example.palitrum.dto.RolePermissionResponse;
import com.example.palitrum.model.Role;
import com.example.palitrum.model.Permission;
import com.example.palitrum.model.RolePermission;
import com.example.palitrum.model.RolePermissionId;
import com.example.palitrum.repository.RolePermissionRepository;
import com.example.palitrum.repository.RoleRepository;
import com.example.palitrum.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepository repository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<RolePermissionResponse> getAll() {
        return repository.findAll().stream()
                .map(rp -> new RolePermissionResponse(rp.getRole().getId(), rp.getPermission().getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public RolePermissionResponse assign(RolePermissionDTO dto) {
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Роль не найдена: " + dto.getRoleId()));
        Permission permission = permissionRepository.findById(dto.getPermissionId())
                .orElseThrow(() -> new RuntimeException("Разрешение не найдено: " + dto.getPermissionId()));

        if (repository.existsByRoleAndPermission(role, permission)) {
            throw new RuntimeException("Данное разрешение уже назначено роли");
        }

        RolePermission rp = RolePermission.builder()
                .role(role)
                .permission(permission)
                .build();
        repository.save(rp);
        return new RolePermissionResponse(role.getId(), permission.getId());
    }

    @Transactional
    public void unassign(Long roleId, Long permissionId) {
        RolePermissionId id = new RolePermissionId(roleId, permissionId);
        if (!repository.existsById(id)) {
            throw new RuntimeException("Связь роль-разрешение не найдена");
        }
        repository.deleteById(id);
    }
}