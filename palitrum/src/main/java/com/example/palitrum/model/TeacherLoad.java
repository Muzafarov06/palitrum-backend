package com.example.palitrum.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "teacher_load")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeacherLoad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "academic_period_id", nullable = false)
    private Long academicPeriodId;

    @Column(name = "staff_id")
    private Long staffId;                         // новое поле

    @Column(name = "weekly_hours_planned", nullable = false, precision = 5, scale = 2)
    private BigDecimal weeklyHoursPlanned;

    @Column(name = "max_weekly_hours", precision = 5, scale = 2)
    private BigDecimal maxWeeklyHours;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}