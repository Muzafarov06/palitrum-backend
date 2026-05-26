package com.example.palitrum.dto.importDTO;

import java.math.BigDecimal;

public record ProgramSubjectImportDTO(String programName, String subjectCode,
                                      Integer academicYear, BigDecimal hoursPerWeekForProgram) {}