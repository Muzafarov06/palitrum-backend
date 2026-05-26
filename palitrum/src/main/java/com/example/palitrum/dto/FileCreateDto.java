package com.example.palitrum.dto;

public record FileCreateDto(
        String entityType,
        Long entityId,
        String storageKey,
        String fileName,
        String fileUrl,
        String fileType,
        Long fileSize,
        Long uploadedById
) {}