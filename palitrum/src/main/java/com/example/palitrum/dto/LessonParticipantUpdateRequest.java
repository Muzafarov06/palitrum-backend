// com.example.palitrum.dto.LessonParticipantUpdateRequest.java
package com.example.palitrum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonParticipantUpdateRequest {

    @NotBlank(message = "Статус посещаемости обязателен")
    private String attendanceStatus;  // "PRESENT", "ABSENT", "LATE", "EXCUSED"

    private String gradeType;
    private String gradeValue;
    private String comment;
}