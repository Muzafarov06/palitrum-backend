package com.example.palitrum.controller;

import com.example.palitrum.dto.UserAuthDto;
import com.example.palitrum.model.User;
import com.example.palitrum.model.UserRole;
import com.example.palitrum.model.Role;
import com.example.palitrum.model.RolePermission;
import com.example.palitrum.repository.UserRepository;
import com.example.palitrum.security.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Неверный email или пароль"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Ошибка аутентификации"));
        }

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        for (UserRole ur : user.getRoles()) {
            if (ur.getRole() == null) continue;
            Role role = ur.getRole();
            roles.add(role.getName());

            // Получаем права через rolePermissions
            if (role.getRolePermissions() != null) {
                for (RolePermission rp : role.getRolePermissions()) {
                    if (rp.getPermission() != null) {
                        permissions.add(rp.getPermission().getCode());
                    }
                }
            }
        }

        // Убираем дубликаты прав (на случай, если роль имеет несколько одинаковых прав)
        permissions = new ArrayList<>(new LinkedHashSet<>(permissions));

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), roles, permissions);

        UserAuthDto dto = new UserAuthDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles,
                permissions
        );

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "user", dto
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Не аутентифицирован"));
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Пользователь не найден"));
        }

        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        for (UserRole ur : user.getRoles()) {
            if (ur.getRole() == null) continue;
            Role role = ur.getRole();
            roles.add(role.getName());

            if (role.getRolePermissions() != null) {
                for (RolePermission rp : role.getRolePermissions()) {
                    if (rp.getPermission() != null) {
                        permissions.add(rp.getPermission().getCode());
                    }
                }
            }
        }

        permissions = new ArrayList<>(new LinkedHashSet<>(permissions));

        UserAuthDto dto = new UserAuthDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles,
                permissions
        );

        return ResponseEntity.ok(dto);
    }

    @Data
    static class LoginRequest {
        private String email;
        private String password;
    }
}