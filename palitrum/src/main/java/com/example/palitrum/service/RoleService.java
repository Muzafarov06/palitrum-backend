package com.example.palitrum.service;

import com.example.palitrum.dto.RoleCreateDto;
import com.example.palitrum.dto.RoleResponseDto;
import com.example.palitrum.model.Role;
import com.example.palitrum.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<RoleResponseDto> getAll(String search, Pageable pageable) {
        Page<Role> page;
        if (search != null && !search.isBlank()) {
            page = roleRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            page = roleRepository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RoleResponseDto getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Роль не найдена: " + id));
        return toDto(role);
    }

    @Transactional
    public RoleResponseDto create(RoleCreateDto dto) {
        String name = dto.getName().trim().toUpperCase();
        if (roleRepository.existsByName(name)) {
            throw new RuntimeException("Роль с именем '" + name + "' уже существует");
        }
        Role role = Role.builder()
                .name(name)
                .description(dto.getDescription())
                .isSystem(dto.isSystem())
                .build();
        return toDto(roleRepository.save(role));
    }

    @Transactional
    public RoleResponseDto update(Long id, RoleCreateDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Роль не найдена: " + id));
        if (role.isSystem()) {
            throw new IllegalArgumentException("Нельзя редактировать системную роль");
        }
        String newName = dto.getName().trim().toUpperCase();
        if (!role.getName().equals(newName) && roleRepository.existsByName(newName)) {
            throw new RuntimeException("Роль с именем '" + newName + "' уже существует");
        }
        role.setName(newName);
        role.setDescription(dto.getDescription());
        role.setSystem(dto.isSystem());
        return toDto(roleRepository.save(role));
    }

    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Роль не найдена: " + id));
        if (role.isSystem()) {
            throw new IllegalArgumentException("Нельзя удалить системную роль");
        }
        roleRepository.deleteById(id);
    }

    private RoleResponseDto toDto(Role role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setSystem(role.isSystem());
        dto.setCreatedAt(role.getCreatedAt());
        return dto;
    }
}