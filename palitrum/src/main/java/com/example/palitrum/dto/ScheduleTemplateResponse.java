package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTemplateResponse {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private Long groupId;
    private String groupName;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private Long roomId;
    private String roomName;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private Integer durationMinutes;
    private Long academicPeriodId;
    private String academicPeriodName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}