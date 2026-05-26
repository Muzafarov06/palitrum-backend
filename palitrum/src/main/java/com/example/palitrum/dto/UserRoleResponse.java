package com.example.palitrum.dto;

import java.time.OffsetDateTime;

public class UserRoleResponse {

    private Long id;
    private Long userId;
    private Long roleId;
    private String scopeType;
    private Long scopeId;
    private Long assignedBy;
    private OffsetDateTime assignedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserRoleResponse() {}

    public UserRoleResponse(Long id, Long userId, Long roleId, String scopeType, Long scopeId,
                            Long assignedBy, OffsetDateTime assignedAt,
                            OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.scopeType = scopeType;
        this.scopeId = scopeId;
        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public String getScopeType() { return scopeType; }
    public void setScopeType(String scopeType) { this.scopeType = scopeType; }

    public Long getScopeId() { return scopeId; }
    public void setScopeId(Long scopeId) { this.scopeId = scopeId; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(OffsetDateTime assignedAt) { this.assignedAt = assignedAt; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
