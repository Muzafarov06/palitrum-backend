package com.example.palitrum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "program_subject",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"program_id", "subject_id", "academic_year"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    @JsonIgnoreProperties({"programSubjects", "programDepartments"})
    private Program program;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({"programSubjects", "defaultProgram"})
    private Subject subject;

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @Column(name = "hours_per_week_for_program", nullable = false)
    private BigDecimal hoursPerWeekForProgram;
}