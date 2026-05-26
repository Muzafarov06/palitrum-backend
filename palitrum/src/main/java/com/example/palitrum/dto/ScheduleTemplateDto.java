package com.example.palitrum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Data
public class ScheduleTemplateDto {
    private Long id; // для обновления

    @NotNull
    private Long subjectId;

    private Long groupId;   // либо groupId, либо studentId
    private Long studentId;

    @NotNull
    private Long teacherId;

    @NotNull
    private Long roomId;

    @NotNull
    @Min(1)
    private Integer dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    @Min(1)
    private Integer durationMinutes;

    @NotNull
    private Long academicPeriodId;
}