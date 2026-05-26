package com.example.palitrum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProgramSubjectDTO {

    private Long id;
    private Long programId;
    private Long subjectId;

    @NotNull(message = "Год обучения обязателен")
    private Integer academicYear;

    @Min(value = 0, message = "Часы не могут быть отрицательными")
    private BigDecimal hoursPerWeekForProgram;
}