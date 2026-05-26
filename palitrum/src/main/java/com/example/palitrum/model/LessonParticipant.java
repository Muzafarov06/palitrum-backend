package com.example.palitrum.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "lesson_participant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lesson_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // enrollment_id временно исключено (при необходимости можно добавить позже)

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false, columnDefinition = "attendance_status_enum NOT NULL")
    @Builder.Default
    private AttendanceStatus attendanceStatus = AttendanceStatus.PRESENT;

    @Column(name = "grade_type", nullable = false, length = 20)
    private String gradeType;

    @Column(name = "grade_value", columnDefinition = "TEXT")
    private String gradeValue;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "recorded_by", nullable = false)
    private Long recordedBy;

    @CreationTimestamp
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private OffsetDateTime recordedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}