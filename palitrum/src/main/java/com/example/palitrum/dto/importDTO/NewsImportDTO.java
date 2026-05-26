package com.example.palitrum.dto.importDTO;

import java.time.OffsetDateTime;

public record NewsImportDTO(
        String title,
        String content,
        String authorEmail,
        Boolean isPublic,
        Boolean pinned,
        OffsetDateTime publishedAt
) {}