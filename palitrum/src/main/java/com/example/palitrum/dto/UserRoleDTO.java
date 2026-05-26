package com.example.palitrum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO {
    private Long userId;
    private Long roleId;
    private String scopeType;
    private Long scopeId;
    private Long assignedBy;
    private OffsetDateTime assignedAt; // ⚡ Используем OffsetDateTime для совпадения с сущностью
}
