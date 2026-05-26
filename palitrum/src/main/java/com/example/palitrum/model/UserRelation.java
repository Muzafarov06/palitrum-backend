package com.example.palitrum.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_relations")
public class UserRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_user_id", nullable = false)
    private Long parentUserId;

    @Column(name = "child_user_id", nullable = false)
    private Long childUserId;

    @Column(name = "relation_type", nullable = false)
    private String relationType; // "parent" или "guardian"

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserRelation() {}

    public UserRelation(Long parentUserId, Long childUserId, String relationType, boolean verified) {
        this.parentUserId = parentUserId;
        this.childUserId = childUserId;
        this.relationType = relationType;
        this.verified = verified;
    }

    // Getters and Setters
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