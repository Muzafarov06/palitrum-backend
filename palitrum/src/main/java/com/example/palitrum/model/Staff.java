package com.example.palitrum.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "staff")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "position_id", nullable = false)
    private Long positionId;

    @Column(name = "rate_count", nullable = false, precision = 4, scale = 2)
    private BigDecimal rateCount;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "dismissal_date")
    private LocalDate dismissalDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}