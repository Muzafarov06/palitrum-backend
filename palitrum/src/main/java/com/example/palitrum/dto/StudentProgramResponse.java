// StudentProgramResponse.java
package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProgramResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long programId;
    private String programName;
    private LocalDate enrollmentDate;
    private LocalDate graduationDate;
    private String status;
}