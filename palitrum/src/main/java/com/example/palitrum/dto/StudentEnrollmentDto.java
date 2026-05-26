package com.example.palitrum.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentEnrollmentDto {
    private Long id;
    private Long studentId;
    private Long programId;
    private LocalDate enrollmentDate;
    private LocalDate graduationDate;
    private String status; // ENROLLED, GRADUATED, DROPPED
}