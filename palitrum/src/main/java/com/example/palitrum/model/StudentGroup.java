package com.example.palitrum.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "student_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "enrolled_date", nullable = false)
    private LocalDate enrolledDate;

    @Column(name = "left_date")
    private LocalDate leftDate;

    @Column(name = "enrollment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "source_application_id")
    private Long sourceApplicationId;

    public enum EnrollmentStatus {
        ENROLLED, GRADUATED, EXPELLED, TRANSFERRED
    }
}