package com.example.palitrum.dto;

import lombok.Data;

@Data
public class ProgramDepartmentDTO {
    private Long programId;
    private Long departmentId;
    private Boolean isPrimary;
    private String notes;
}
