package com.example.palitrum.dto;

import lombok.Data;

@Data
public class GroupDTO {
    private Long programId;
    private String name;
    private String level;
    private Integer maxStudents;
    private String status;
    private Integer academicYear;
    private Long subjectId;
}