package com.example.palitrum.dto.importDTO;

import java.time.LocalDate;

public record AcademicPeriodImportDTO(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String periodType, // SEMESTER, QUARTER, YEAR
        Boolean isCurrent
) {}