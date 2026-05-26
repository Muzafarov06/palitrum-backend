package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeDto {
    private Long subjectId;          // <-- добавлено
    private String subjectName;
    private double averageGrade;
    private int absences;
}