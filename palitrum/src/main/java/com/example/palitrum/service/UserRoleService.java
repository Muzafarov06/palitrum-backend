package com.example.palitrum.service;

import com.example.palitrum.dto.UserRoleDTO;
import com.example.palitrum.dto.UserRoleResponse;
import com.example.palitrum.model.User;
import com.example.palitrum.model.Role;
import com.example.palitrum.model.UserRole;
import com.example.palitrum.repository.UserRepository;
import com.example.palitrum.repository.RoleRepository;
import com.example.palitrum.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    private final UserRoleRepository repository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(
            UserRoleRepository repository,
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    // в UserRoleService
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

    // Получение всех связей User ↔ Role
    public List<UserRoleResponse> getAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Получение связей по фильтру userId или roleId
    public List<UserRoleResponse> getByUserOrRole(Long userId, Long roleId) {
        return repository.findAll().stream()
                .filter(ur -> (userId == null || ur.getUser().getId().equals(userId)) &&
                        (roleId == null || ur.getRole().getId().equals(roleId)))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Создание связи User ↔ Role с проверкой уникальности
    public UserRoleResponse create(UserRoleDTO dto) {

        // Получаем пользователя
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем роль
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Получаем пользователя, который назначил роль
        User assignedBy = null;
        if (dto.getAssignedBy() != null) {
            assignedBy = userRepository.findById(dto.getAssignedBy()).orElse(null);
        }

        // Проверка на существующую связь
        boolean exists = repository.findAll().stream().anyMatch(ur ->
                ur.getUser().getId().equals(dto.getUserId()) &&
                        ur.getRole().getId().equals(dto.getRoleId()) &&
                        ((ur.getScopeType() == null && dto.getScopeType() == null) ||
                                (ur.getScopeType() != null && ur.getScopeType().equals(dto.getScopeType()))) &&
                        ((ur.getScopeId() == null && dto.getScopeId() == null) ||
                                (ur.getScopeId() != null && ur.getScopeId().equals(dto.getScopeId())))
        );
        if (exists) {
            throw new RuntimeException("UserRole already exists");
        }

        UserRole ur = new UserRole();
        ur.setUser(user);
        ur.setRole(role);
        ur.setScopeType(dto.getScopeType());
        ur.setScopeId(dto.getScopeId());
        ur.setAssignedBy(assignedBy);
        ur.setAssignedAt(dto.getAssignedAt() != null ? dto.getAssignedAt() : OffsetDateTime.now());

        UserRole saved = repository.save(ur);
        return mapToResponse(saved);

    }

    // Удаление связи
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Маппинг UserRole → UserRoleResponse
    private UserRoleResponse mapToResponse(UserRole ur) {
        return new UserRoleResponse(
                ur.getId(),
                ur.getUser().getId(),
                ur.getRole().getId(),
                ur.getScopeType(),
                ur.getScopeId(),
                ur.getAssignedBy() != null ? ur.getAssignedBy().getId() : null,
                ur.getAssignedAt(),
                ur.getCreatedAt(),
                ur.getUpdatedAt()
        );
    }
}
