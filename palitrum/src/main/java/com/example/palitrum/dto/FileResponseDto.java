package com.example.palitrum.dto;

public record FileResponseDto(
        Long id,
        String entityType,
        Long entityId,
        String storageKey,
        String fileName,
        String fileUrl,
        String fileType,
        Long fileSize,
        Long uploadedById,
        String uploadedAt,
        String updatedAt
) {}