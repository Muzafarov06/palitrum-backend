package com.example.palitrum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolePermissionDTO {
    @NotNull(message = "ID роли обязателен")
    private Long roleId;

    @NotNull(message = "ID разрешения обязателен")
    private Long permissionId;
}