// com.example.palitrum.dto.LessonParticipantResponse.java
package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonParticipantResponse {
    private Long id;
    private Long studentId;
    private String studentFullName;
    private String attendanceStatus;
    private String gradeType;
    private String gradeValue;
    private String comment;
    private OffsetDateTime updatedAt;
}