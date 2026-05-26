package com.example.palitrum.dto;

import java.time.LocalDateTime;

public class UserRelationResponse {

    private Long id;
    private Long parentUserId;
    private Long childUserId;
    private String relationType;
    private boolean verified;
    private LocalDateTime createdAt;

    public UserRelationResponse() {}

    public UserRelationResponse(Long id, Long parentUserId, Long childUserId, String relationType, boolean verified, LocalDateTime createdAt) {
        this.id = id;
        this.parentUserId = parentUserId;
        this.childUserId = childUserId;
        this.relationType = relationType;
        this.verified = verified;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getParentUserId() { return parentUserId; }
    public void setParentUserId(Long parentUserId) { this.parentUserId = parentUserId; }

    public Long getChildUserId() { return childUserId; }
    public void setChildUserId(Long childUserId) { this.childUserId = childUserId; }

    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
