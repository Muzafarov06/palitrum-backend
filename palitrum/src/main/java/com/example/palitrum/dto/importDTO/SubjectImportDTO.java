package com.example.palitrum.dto.importDTO;

public record SubjectImportDTO(String code, String name, String description,
                               Integer standardHoursPerWeek, String lessonType,
                               Integer minGroupSize, Integer maxGroupSize,
                               String defaultProgramName) {}