package com.example.palitrum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TeacherLoadDto {

    @NotNull(message = "ID преподавателя обязателен")
    private Long teacherId;

    @NotNull(message = "ID учебного периода обязателен")
    private Long academicPeriodId;

    private Long staffId;                         // новое поле

    @NotNull(message = "Плановые часы обязательны")
    @PositiveOrZero(message = "Часы не могут быть отрицательными")
    private BigDecimal weeklyHoursPlanned;

    @Positive(message = "Максимальные часы должны быть положительными")
    private BigDecimal maxWeeklyHours;
}