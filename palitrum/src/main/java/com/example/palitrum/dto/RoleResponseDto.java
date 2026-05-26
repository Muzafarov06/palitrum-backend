package com.example.palitrum.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class RoleResponseDto {
    private Long id;
    private String name;
    private String description;
    private boolean isSystem;
    private OffsetDateTime createdAt;
}