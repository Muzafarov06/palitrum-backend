package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicPeriodResponse {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String periodType;
    private Boolean isCurrent;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}