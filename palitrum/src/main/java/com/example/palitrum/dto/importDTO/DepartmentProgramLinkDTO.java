// DepartmentProgramLinkDTO.java
package com.example.palitrum.dto.importDTO;

public record DepartmentProgramLinkDTO(
        String departmentName,
        String programName,
        Boolean isPrimary,
        String notes
) {}