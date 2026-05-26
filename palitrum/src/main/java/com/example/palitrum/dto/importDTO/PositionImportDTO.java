package com.example.palitrum.dto.importDTO;

import java.math.BigDecimal;

public record PositionImportDTO(
        String name,
        BigDecimal hoursPerRate,
        Boolean isTeaching
) {}