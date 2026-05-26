package com.example.palitrum.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class ApplicationResponseDto {

    private Long id;

    // ---------- Данные ребёнка ----------
    private String childLastName;
    private String childFirstName;
    private String childMiddleName;
    private LocalDate childBirthDate;
    private String childBirthPlace;
    private String childCitizenship;
    private String childAddress;
    private String childSnils;
    private Boolean childIndividualPlan;
    private String childLastSchool;
    private String childGradeLevel;

    // ---------- Данные родителя ----------
    private String parentLastName;
    private String parentFirstName;
    private String parentMiddleName;
    private String parentRelation;
    private String parentPhone;
    private String parentEmail;

    // ---------- Программы ----------
    private Long directionId;           // устаревшее, можно не использовать
    private Long preferredProgramId;
    private String preferredProgramName;
    private Long finalProgramId;
    private String finalProgramName;

    // ---------- Согласия ----------
    private Boolean consentPersonalData;
    private Boolean consentPhotoVideo;
    private Boolean consentMedicalIntervention;

    // ---------- Доп. информация ----------
    private String additionalInfo;

    // ---------- Статусная модель ----------
    private String status;
    private String rejectionReason;
    private Long assignedOfficerId;
    private String source;              // SITE, PHONE, MANUAL, EMAIL

    // ---------- Временные метки процесса ----------
    private OffsetDateTime submittedAt;
    private OffsetDateTime reviewedAt;
    private OffsetDateTime decisionAt;
    private LocalDate enrollmentDate;
    private LocalDate waitlistExpiryDate;

    // ---------- Комментарии и история ----------
    private String internalNotes;
    private String officerComment;
    private String historyChanges;      // JSON

    // ---------- Связь с созданными пользователями ----------
    private Long childUserId;
    private Long parentUserId;

    // ---------- Системные поля ----------
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Конструкторы
    public ApplicationResponseDto() {}

    // Геттеры и сеттеры

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChildLastName() { return childLastName; }
    public void setChildLastName(String childLastName) { this.childLastName = childLastName; }

    public String getChildFirstName() { return childFirstName; }
    public void setChildFirstName(String childFirstName) { this.childFirstName = childFirstName; }

    public String getChildMiddleName() { return childMiddleName; }
    public void setChildMiddleName(String childMiddleName) { this.childMiddleName = childMiddleName; }

    public LocalDate getChildBirthDate() { return childBirthDate; }
    public void setChildBirthDate(LocalDate childBirthDate) { this.childBirthDate = childBirthDate; }

    public String getChildBirthPlace() { return childBirthPlace; }
    public void setChildBirthPlace(String childBirthPlace) { this.childBirthPlace = childBirthPlace; }

    public String getChildCitizenship() { return childCitizenship; }
    public void setChildCitizenship(String childCitizenship) { this.childCitizenship = childCitizenship; }

    public String getChildAddress() { return childAddress; }
    public void setChildAddress(String childAddress) { this.childAddress = childAddress; }

    public String getChildSnils() { return childSnils; }
    public void setChildSnils(String childSnils) { this.childSnils = childSnils; }

    public Boolean getChildIndividualPlan() { return childIndividualPlan; }
    public void setChildIndividualPlan(Boolean childIndividualPlan) { this.childIndividualPlan = childIndividualPlan; }

    public String getChildLastSchool() { return childLastSchool; }
    public void setChildLastSchool(String childLastSchool) { this.childLastSchool = childLastSchool; }

    public String getChildGradeLevel() { return childGradeLevel; }
    public void setChildGradeLevel(String childGradeLevel) { this.childGradeLevel = childGradeLevel; }

    public String getParentLastName() { return parentLastName; }
    public void setParentLastName(String parentLastName) { this.parentLastName = parentLastName; }

    public String getParentFirstName() { return parentFirstName; }
    public void setParentFirstName(String parentFirstName) { this.parentFirstName = parentFirstName; }

    public String getParentMiddleName() { return parentMiddleName; }
    public void setParentMiddleName(String parentMiddleName) { this.parentMiddleName = parentMiddleName; }

    public String getParentRelation() { return parentRelation; }
    public void setParentRelation(String parentRelation) { this.parentRelation = parentRelation; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }

    public Long getDirectionId() { return directionId; }
    public void setDirectionId(Long directionId) { this.directionId = directionId; }

    public Long getPreferredProgramId() { return preferredProgramId; }
    public void setPreferredProgramId(Long preferredProgramId) { this.preferredProgramId = preferredProgramId; }

    public String getPreferredProgramName() { return preferredProgramName; }
    public void setPreferredProgramName(String preferredProgramName) { this.preferredProgramName = preferredProgramName; }

    public Long getFinalProgramId() { return finalProgramId; }
    public void setFinalProgramId(Long finalProgramId) { this.finalProgramId = finalProgramId; }

    public String getFinalProgramName() { return finalProgramName; }
    public void setFinalProgramName(String finalProgramName) { this.finalProgramName = finalProgramName; }

    public Boolean getConsentPersonalData() { return consentPersonalData; }
    public void setConsentPersonalData(Boolean consentPersonalData) { this.consentPersonalData = consentPersonalData; }

    public Boolean getConsentPhotoVideo() { return consentPhotoVideo; }
    public void setConsentPhotoVideo(Boolean consentPhotoVideo) { this.consentPhotoVideo = consentPhotoVideo; }

    public Boolean getConsentMedicalIntervention() { return consentMedicalIntervention; }
    public void setConsentMedicalIntervention(Boolean consentMedicalIntervention) { this.consentMedicalIntervention = consentMedicalIntervention; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Long getAssignedOfficerId() { return assignedOfficerId; }
    public void setAssignedOfficerId(Long assignedOfficerId) { this.assignedOfficerId = assignedOfficerId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }

    public OffsetDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(OffsetDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public OffsetDateTime getDecisionAt() { return decisionAt; }
    public void setDecisionAt(OffsetDateTime decisionAt) { this.decisionAt = decisionAt; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public LocalDate getWaitlistExpiryDate() { return waitlistExpiryDate; }
    public void setWaitlistExpiryDate(LocalDate waitlistExpiryDate) { this.waitlistExpiryDate = waitlistExpiryDate; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public String getOfficerComment() { return officerComment; }
    public void setOfficerComment(String officerComment) { this.officerComment = officerComment; }

    public String getHistoryChanges() { return historyChanges; }
    public void setHistoryChanges(String historyChanges) { this.historyChanges = historyChanges; }

    public Long getChildUserId() { return childUserId; }
    public void setChildUserId(Long childUserId) { this.childUserId = childUserId; }

    public Long getParentUserId() { return parentUserId; }
    public void setParentUserId(Long parentUserId) { this.parentUserId = parentUserId; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}