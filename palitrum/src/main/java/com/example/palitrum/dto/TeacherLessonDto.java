// com.example.palitrum.dto.TeacherLessonDto.java
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
public class TeacherLessonDto {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subjectName;
    private String groupName;      // если занятие групповое
    private String studentName;    // если индивидуальное
}