// ScheduleGenerationRequest.java
package com.example.palitrum.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ScheduleGenerationRequest {
    private Long periodId;           // ID учебного периода
    private LocalDate startDate;     // дата начала генерации
    private LocalDate endDate;       // дата окончания генерации
    private Boolean generateGroups = true;    // генерировать групповые занятия
    private Boolean generateIndividual = true; // генерировать индивидуальные занятия
    private Boolean autoAssignTeachers = true; // автоматически назначать преподавателей
    private Boolean autoAssignRooms = true;    // автоматически назначать аудитории
}