package com.example.palitrum.dto;

public class ProgramDepartmentResponse {

    private Long id;
    private Long programId;
    private Long departmentId;
    private Boolean isPrimary;
    private String notes;

    public ProgramDepartmentResponse() {}

    public ProgramDepartmentResponse(
            Long id,
            Long programId,
            Long departmentId,
            Boolean isPrimary,
            String notes
    ) {
        this.id = id;
        this.programId = programId;
        this.departmentId = departmentId;
        this.isPrimary = isPrimary;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProgramId() { return programId; }
    public void setProgramId(Long programId) { this.programId = programId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
