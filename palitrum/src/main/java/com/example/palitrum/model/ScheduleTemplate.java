package com.example.palitrum.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "schedule_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "room_id", nullable = true)  // ← изменено на nullable = true
    private Long roomId;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 1 = понедельник, 7 = воскресенье

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "academic_period_id", nullable = false)
    private Long academicPeriodId;

    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}