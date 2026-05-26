package com.example.palitrum.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "student_subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_program_id", nullable = false)
    private Long studentProgramId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "planned_hours_per_week", nullable = false)
    private BigDecimal plannedHoursPerWeek;

    @Column(name = "is_group_lesson", nullable = false)
    private Boolean isGroupLesson;

    @Column(name = "group_id")
    private Long groupId;
}