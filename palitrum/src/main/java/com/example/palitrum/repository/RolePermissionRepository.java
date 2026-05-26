package com.example.palitrum.repository;

import com.example.palitrum.model.Role;
import com.example.palitrum.model.Permission;
import com.example.palitrum.model.RolePermission;
import com.example.palitrum.model.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    boolean existsByRoleAndPermission(Role role, Permission permission);
}