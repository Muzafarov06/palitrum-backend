package com.example.palitrum.repository;

import com.example.palitrum.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // ⚡ Найти все роли конкретного пользователя
    List<UserRole> findByUserId(Long userId);

    // ⚡ Найти все пользователей с конкретной ролью
    List<UserRole> findByRoleId(Long roleId);

    // ⚡ Проверка уникальности связи user-role
    boolean existsByUserIdAndRoleIdAndScopeTypeAndScopeId(Long userId, Long roleId, String scopeType, Long scopeId);
}
