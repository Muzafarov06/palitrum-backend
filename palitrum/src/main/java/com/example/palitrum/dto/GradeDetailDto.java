package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDetailDto {
    private LocalDate date;
    private LocalTime time;
    private String subjectName;
    private String programName;
    private String groupName;          // для групповых занятий
    private String studentName;        // для индивидуальных
    private String gradeValue;
    private String attendanceStatus;   // PRESENT, ABSENT и т.д.
}