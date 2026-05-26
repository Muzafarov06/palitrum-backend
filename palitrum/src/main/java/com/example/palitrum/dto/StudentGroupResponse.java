package com.example.palitrum.dto;

import java.time.LocalDate;

public class StudentGroupResponse {

    private Long id;
    private Long userId;
    private Long groupId;
    private LocalDate enrolledDate;
    private LocalDate leftDate;
    private String enrollmentStatus;
    private boolean active;
    private Long sourceApplicationId;
    private String userFullName;

    public StudentGroupResponse() {}

    public StudentGroupResponse(Long id, Long userId, Long groupId, LocalDate enrolledDate, LocalDate leftDate,
                                String enrollmentStatus, boolean active, Long sourceApplicationId) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.enrolledDate = enrolledDate;
        this.leftDate = leftDate;
        this.enrollmentStatus = enrollmentStatus;
        this.active = active;
        this.sourceApplicationId = sourceApplicationId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public LocalDate getEnrolledDate() { return enrolledDate; }
    public void setEnrolledDate(LocalDate enrolledDate) { this.enrolledDate = enrolledDate; }

    public LocalDate getLeftDate() { return leftDate; }
    public void setLeftDate(LocalDate leftDate) { this.leftDate = leftDate; }

    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Long getSourceApplicationId() { return sourceApplicationId; }
    public void setSourceApplicationId(Long sourceApplicationId) { this.sourceApplicationId = sourceApplicationId; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
}