package com.example.palitrum.service;

import com.example.palitrum.dto.UserCreateDto;
import com.example.palitrum.dto.UserResponseDto;
import com.example.palitrum.dto.UserUpdateDto;
import com.example.palitrum.model.Application;
import com.example.palitrum.model.User;
import com.example.palitrum.model.UserRole;
import com.example.palitrum.repository.ApplicationRepository;
import com.example.palitrum.repository.UserRepository;
import com.example.palitrum.util.TranslitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final Set<String> ALLOWED_STATUSES = Set.of("PENDING", "ACTIVE", "BLOCKED", "ARCHIVED");

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final ApplicationRepository applicationRepository;

    public UserService(UserRepository repo, PasswordEncoder encoder, ApplicationRepository applicationRepository) {
        this.repo = repo;
        this.encoder = encoder;
        this.applicationRepository = applicationRepository;
    }

    // ----- Вспомогательные методы -----
    public Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return null;
        }
        return ((UserDetailsImpl) auth.getPrincipal()).getUserId();
    }

    private boolean isSuperAdmin(User user) {
        if (user == null || user.getRoles() == null) return false;
        return user.getRoles().stream()
                .map(UserRole::getRole)
                .anyMatch(role -> role != null && "SUPER_ADMIN".equals(role.getName()));
    }

    // ----- Основные методы -----
    public UserResponseDto getById(Long id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + id + " не найден"));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponseDto create(UserCreateDto dto) {
        if (repo.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        if (repo.existsByPhone(dto.phone())) {
            throw new IllegalArgumentException("Пользователь с таким телефоном уже существует");
        }

        User u = new User();
        u.setFirstName(dto.firstName());
        u.setLastName(dto.lastName());
        u.setMiddleName(dto.middleName());
        u.setEmail(dto.email());
        u.setPhone(dto.phone());
        u.setPasswordHash(encoder.encode(dto.password()));
        u.setBirthDate(dto.birthDate());
        u.setStatus("ACTIVE");
        u.setStaff(dto.isStaff());

        User saved = repo.save(u);
        log.info("Создан новый пользователь: {} {} (id={})", u.getFirstName(), u.getLastName(), u.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto dto) {
        User u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (isSuperAdmin(u)) {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(u.getId())) {
                throw new SecurityException("Невозможно изменить другого SUPER_ADMIN");
            }
        }

        if (dto.email() != null) {
            repo.findByEmail(dto.email()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Пользователь с таким email уже существует");
                }
            });
            u.setEmail(dto.email());
        }

        if (dto.phone() != null) {
            if (repo.existsByPhoneAndIdNot(dto.phone(), id)) {
                throw new IllegalArgumentException("Пользователь с таким телефоном уже существует");
            }
            u.setPhone(dto.phone());
        }

        if (dto.firstName() != null) u.setFirstName(dto.firstName());
        if (dto.lastName() != null) u.setLastName(dto.lastName());
        if (dto.middleName() != null) u.setMiddleName(dto.middleName());
        if (dto.birthDate() != null) u.setBirthDate(dto.birthDate());
        if (dto.isStaff() != null) u.setStaff(dto.isStaff());

        if (dto.status() != null && !dto.status().isBlank()) {
            if (!ALLOWED_STATUSES.contains(dto.status().toUpperCase())) {
                throw new IllegalArgumentException("Недопустимый статус. Разрешены: " + ALLOWED_STATUSES);
            }
            u.setStatus(dto.status().toUpperCase());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            u.setPasswordHash(encoder.encode(dto.password()));
        }

        User saved = repo.save(u);
        log.info("Обновлён пользователь id={}", id);
        return mapToResponse(saved);
    }

    public List<UserResponseDto> getAll(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage;
        if (search != null && !search.isBlank()) {
            usersPage = repo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, search, pageable);
        } else {
            usersPage = repo.findAll(pageable);
        }
        return usersPage.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<UserResponseDto> getFilteredUsers(String status, String roleName, String search,
                                                  LocalDate birthDateFrom, LocalDate birthDateTo,
                                                  Pageable pageable) {
        Page<User> page = repo.searchUsers(status, roleName, search, birthDateFrom, birthDateTo, pageable);
        return page.map(this::mapToResponse);
    }

    public Map<String, Long> getUserStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", repo.count());
        stats.put("active", repo.countByStatus("ACTIVE"));
        stats.put("pending", repo.countByStatus("PENDING"));
        stats.put("blocked", repo.countByStatus("BLOCKED"));
        stats.put("archived", repo.countByStatus("ARCHIVED"));
        stats.put("students", repo.countByStatusAndRole("ACTIVE", "STUDENT"));
        stats.put("teachers", repo.countByStatusAndRole("ACTIVE", "TEACHER"));
        stats.put("managers", repo.countByStatusAndRole("ACTIVE", "MANAGER"));
        stats.put("admins", repo.countByStatusAndRole("ACTIVE", "ADMIN"));
        stats.put("superAdmins", repo.countByStatusAndRole("ACTIVE", "SUPER_ADMIN"));
        stats.put("parents", repo.countByStatusAndRole("ACTIVE", "PARENT"));
        return stats;
    }

    @Transactional
    public UserResponseDto updateStatus(Long id, String status) {
        User u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (!ALLOWED_STATUSES.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Недопустимый статус. Разрешены: " + ALLOWED_STATUSES);
        }

        if (isSuperAdmin(u)) {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(u.getId())) {
                throw new SecurityException("Невозможно изменить статус другого SUPER_ADMIN");
            }
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(u.getId()) && !isSuperAdmin(u)) {
            throw new SecurityException("Вы не можете изменить свой собственный статус");
        }

        u.setStatus(status.toUpperCase());
        User saved = repo.save(u);
        log.info("Статус пользователя id={} изменён на {}", id, status);
        return mapToResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        User u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (isSuperAdmin(u)) {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(u.getId())) {
                throw new SecurityException("Невозможно удалить другого SUPER_ADMIN");
            }
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(u.getId())) {
            throw new SecurityException("Вы не можете удалить свой собственный аккаунт");
        }

        repo.deleteById(id);
        log.info("Удалён пользователь id={}", id);
    }

    @Transactional
    public User createChildFromApplication(Application app, String rawPassword) {
        String childEmail = TranslitUtils.toLatin(app.getChildLastName()) + "." +
                TranslitUtils.toLatin(app.getChildFirstName()) + "@school.ru";
        childEmail = childEmail.replaceAll("\\.+", ".");
        String originalEmail = childEmail;
        int counter = 1;
        while (repo.existsByEmail(childEmail)) {
            childEmail = originalEmail.replace("@", counter + "@");
            counter++;
        }

        User child = new User();
        child.setFirstName(app.getChildFirstName());
        child.setLastName(app.getChildLastName());
        child.setMiddleName(app.getChildMiddleName());
        child.setEmail(childEmail);
        child.setPhone(null);
        child.setPasswordHash(encoder.encode(rawPassword));
        child.setBirthDate(app.getChildBirthDate());
        child.setStatus("ACTIVE");
        child.setStaff(false);

        return repo.save(child);
    }

    @Transactional
    public User createParentFromApplication(Application app, String rawPassword) {
        User existing = repo.findByEmail(app.getParentEmail()).orElse(null);
        if (existing != null) {
            return existing;
        }

        User parent = new User();
        parent.setFirstName(app.getParentFirstName());
        parent.setLastName(app.getParentLastName());
        parent.setMiddleName(app.getParentMiddleName());
        parent.setEmail(app.getParentEmail());
        parent.setPhone(app.getParentPhone());
        parent.setPasswordHash(encoder.encode(rawPassword));
        parent.setBirthDate(null);
        parent.setStatus("ACTIVE");
        parent.setStaff(false);

        return repo.save(parent);
    }

    // Публичный метод для маппинга User -> UserResponseDto
    public UserResponseDto mapToResponse(User user) {
        UserResponseDto out = new UserResponseDto();
        out.setId(user.getId());
        out.setFirstName(user.getFirstName());
        out.setLastName(user.getLastName());
        out.setMiddleName(user.getMiddleName());
        out.setEmail(user.getEmail());
        out.setPhone(user.getPhone());
        out.setBirthDate(user.getBirthDate());
        out.setStatus(user.getStatus());
        out.setStaff(user.isStaff());

        out.setRoles(user.getRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList()));

        out.setPermissions(List.of()); // если нет прав – пустой список

        out.setCreatedAt(user.getCreatedAt() != null
                ? user.getCreatedAt().withOffsetSameInstant(ZoneOffset.UTC)
                : null);
        out.setUpdatedAt(user.getUpdatedAt() != null
                ? user.getUpdatedAt().withOffsetSameInstant(ZoneOffset.UTC)
                : null);

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            UserRole firstRole = user.getRoles().iterator().next();
            out.setRoleAssignedAt(firstRole.getAssignedAt() != null
                    ? firstRole.getAssignedAt().withOffsetSameInstant(ZoneOffset.UTC)
                    : null);
            out.setRoleCreatedAt(firstRole.getCreatedAt() != null
                    ? firstRole.getCreatedAt().withOffsetSameInstant(ZoneOffset.UTC)
                    : null);
            out.setRoleUpdatedAt(firstRole.getUpdatedAt() != null
                    ? firstRole.getUpdatedAt().withOffsetSameInstant(ZoneOffset.UTC)
                    : null);
        }

        applicationRepository.findByUserId(user.getId()).ifPresent(app -> {
            out.setSourceApplicationId(app.getId());
            out.setSourceApplicationStatus(app.getStatus().getValue());
            out.setSourceApplicationCreatedAt(app.getCreatedAt());
        });

        return out;
    }
}