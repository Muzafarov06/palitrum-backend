package com.example.palitrum.dto;

import java.time.LocalDateTime;

public class GroupResponse {
    private Long id;
    private Long programId;
    private String name;
    private String level;
    private Integer maxStudents;
    private String status;
    private Integer academicYear;
    private Long subjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GroupResponse() {}

    public GroupResponse(Long id, Long programId, String name, String level, Integer maxStudents,
                         String status, Integer academicYear, Long subjectId,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.programId = programId;
        this.name = name;
        this.level = level;
        this.maxStudents = maxStudents;
        this.status = status;
        this.academicYear = academicYear;
        this.subjectId = subjectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProgramId() { return programId; }
    public void setProgramId(Long programId) { this.programId = programId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAcademicYear() { return academicYear; }
    public void setAcademicYear(Integer academicYear) { this.academicYear = academicYear; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}