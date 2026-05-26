package com.example.palitrum.controller;

import com.example.palitrum.dto.UserCreateDto;
import com.example.palitrum.dto.UserResponseDto;
import com.example.palitrum.dto.UserUpdateDto;
import com.example.palitrum.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user.view')")
    public List<UserResponseDto> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        return service.getAll(search, 0, 1000).stream()
                .filter(u -> status == null || u.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('user.view')")
    public Page<UserResponseDto> filterUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String search,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDateTo,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return service.getFilteredUsers(status, roleName, search, birthDateFrom, birthDateTo, pageable);
    }

    // НОВЫЙ МЕТОД: получение пользователя по ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user.view')")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('user.view')")
    public Map<String, Long> getUserStatistics() {
        return service.getUserStatistics();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user.create')")
    public UserResponseDto create(@Valid @RequestBody UserCreateDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user.update')")
    public UserResponseDto update(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('user.block', 'user.unblock')")
    public UserResponseDto updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user.delete')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}