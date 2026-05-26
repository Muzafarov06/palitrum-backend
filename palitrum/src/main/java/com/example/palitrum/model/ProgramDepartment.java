    package com.example.palitrum.model;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;
    import lombok.*;

    @Entity
    @Table(
            name = "program_department",
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = {"program_id", "department_id"})
            }
    )
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ProgramDepartment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "program_id", nullable = false)
        @JsonIgnoreProperties({"programDepartments"})
        private Program program;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "department_id", nullable = false)
        @JsonIgnoreProperties({"programDepartments"})
        private Department department;

        @Column(name = "is_primary")
        private Boolean isPrimary;

        @Column(columnDefinition = "TEXT")
        private String notes;
    }
