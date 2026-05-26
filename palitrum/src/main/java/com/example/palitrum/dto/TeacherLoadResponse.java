package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherLoadResponse {
    private Long id;
    private Long teacherId;
    private String teacherName;
    private Long academicPeriodId;
    private String periodName;
    private Long staffId;                         // новое поле
    private String positionName;                  // название должности
    private BigDecimal rateCount;                 // количество ставок
    private BigDecimal weeklyHoursPlanned;
    private BigDecimal maxWeeklyHours;
}