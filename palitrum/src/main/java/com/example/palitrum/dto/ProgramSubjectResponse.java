package com.example.palitrum.dto;

import java.math.BigDecimal;

public class ProgramSubjectResponse {

    private Long id;
    private Long programId;
    private Long subjectId;
    private String subjectName;
    private Integer academicYear;
    private BigDecimal hoursPerWeekForProgram;

    public ProgramSubjectResponse() {}

    public ProgramSubjectResponse(Long id, Long programId, Long subjectId,
                                  String subjectName, Integer academicYear,
                                  BigDecimal hoursPerWeekForProgram) {
        this.id = id;
        this.programId = programId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.academicYear = academicYear;
        this.hoursPerWeekForProgram = hoursPerWeekForProgram;
    }

    // геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProgramId() { return programId; }
    public void setProgramId(Long programId) { this.programId = programId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Integer getAcademicYear() { return academicYear; }
    public void setAcademicYear(Integer academicYear) { this.academicYear = academicYear; }

    public BigDecimal getHoursPerWeekForProgram() { return hoursPerWeekForProgram; }
    public void setHoursPerWeekForProgram(BigDecimal hoursPerWeekForProgram) {
        this.hoursPerWeekForProgram = hoursPerWeekForProgram;
    }
}