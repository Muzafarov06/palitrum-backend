package com.example.palitrum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleCreateDto {
    @NotBlank(message = "Название роли обязательно")
    @Size(max = 50, message = "Название не должно превышать 50 символов")
    private String name;

    private String description;

    private boolean isSystem;
}