package com.example.palitrum.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------- Данные ребёнка ----------
    @Column(name = "child_last_name", nullable = false, columnDefinition = "TEXT")
    private String childLastName;

    @Column(name = "child_first_name", nullable = false, columnDefinition = "TEXT")
    private String childFirstName;

    @Column(name = "child_middle_name")
    private String childMiddleName;

    @Column(name = "child_birth_date", nullable = false)
    private LocalDate childBirthDate;

    @Column(name = "child_birth_place")
    private String childBirthPlace;

    @Column(name = "child_citizenship")
    private String childCitizenship;

    @Column(name = "child_address")
    private String childAddress;

    @Column(name = "child_snils")
    private String childSnils;

    @Builder.Default
    @Column(name = "child_individual_plan")
    private Boolean childIndividualPlan = false;

    @Column(name = "child_last_school")
    private String childLastSchool;

    @Column(name = "child_grade_level")
    private String childGradeLevel;

    // ---------- Данные родителя ----------
    @Column(name = "parent_last_name", nullable = false)
    private String parentLastName;

    @Column(name = "parent_first_name", nullable = false)
    private String parentFirstName;

    @Column(name = "parent_middle_name")
    private String parentMiddleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_relation", nullable = false)
    @Builder.Default
    private ParentRelation parentRelation = ParentRelation.MOTHER;

    @Column(name = "parent_phone", nullable = false)
    private String parentPhone;

    @Column(name = "parent_email", nullable = false)
    private String parentEmail;

    // ---------- Программы ----------
    @ManyToOne
    @JoinColumn(name = "preferred_program_id")
    private Program preferredProgram;

    @ManyToOne
    @JoinColumn(name = "final_program_id")
    private Program finalProgram;

    // ---------- Согласия ----------
    @Builder.Default
    @Column(name = "consent_personal_data", nullable = false)
    private Boolean consentPersonalData = false;

    @Builder.Default
    @Column(name = "consent_photo_video", nullable = false)
    private Boolean consentPhotoVideo = false;

    @Builder.Default
    @Column(name = "consent_medical_intervention", nullable = false)
    private Boolean consentMedicalIntervention = false;

    // ---------- Доп. информация ----------
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    // ---------- Статус и обработка ----------
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.NEW;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @ManyToOne
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    @Builder.Default
    private Source source = Source.SITE;

    // ---------- Временные метки процесса ----------
    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Column(name = "decision_at")
    private OffsetDateTime decisionAt;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "waitlist_expiry_date")
    private LocalDate waitlistExpiryDate;

    // ---------- Комментарии и история ----------
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "officer_comment", columnDefinition = "TEXT")
    private String officerComment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "history_changes", columnDefinition = "jsonb")
    private String historyChanges;

    // ---------- Связь с пользователями ----------
    @Column(name = "child_user_id")
    private Long childUserId;

    @Column(name = "parent_user_id")
    private Long parentUserId;

    // ---------- Системные поля ----------
    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    // ----- Вспомогательные методы -----
    public Long getPreferredProgramId() {
        return preferredProgram != null ? preferredProgram.getId() : null;
    }

    public Long getFinalProgramId() {
        return finalProgram != null ? finalProgram.getId() : null;
    }

    public Long getAssignedOfficerId() {
        return assignedOfficer != null ? assignedOfficer.getId() : null;
    }

    // ----- Перечисления -----
    public enum Status {
        NEW("NEW"),
        REVIEWED("REVIEWED"),
        WAITING_DOCS("WAITING_DOCS"),
        ACCEPTED("ACCEPTED"),
        REJECTED("REJECTED"),
        WAITLIST("WAITLIST");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    public enum Source {
        SITE("SITE"),
        PHONE("PHONE"),
        MANUAL("MANUAL"),
        EMAIL("EMAIL");

        private final String value;

        Source(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    public enum ParentRelation {
        MOTHER, FATHER, GUARDIAN
    }
}