package com.example.palitrum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "standard_hours_per_week", nullable = false)
    private Integer standardHoursPerWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_program_id")
    @JsonIgnoreProperties({"subjects", "programDepartments"})
    private Program defaultProgram;

    @Column(name = "lesson_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LessonType lessonType;

    @Column(name = "min_group_size")
    private Integer minGroupSize;

    @Column(name = "max_group_size")
    private Integer maxGroupSize;
}