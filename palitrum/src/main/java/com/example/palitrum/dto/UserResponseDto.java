package com.example.palitrum.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String status;
    private boolean isStaff;
    private List<String> roles;
    private List<String> permissions;

    // Поля из таблицы users (даты создания и изменения пользователя)
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Поля из таблицы user_role (информация о назначении роли)
    private OffsetDateTime roleAssignedAt;
    private OffsetDateTime roleCreatedAt;
    private OffsetDateTime roleUpdatedAt;

    // НОВЫЕ ПОЛЯ: ссылка на заявку, из которой создан пользователь
    private Long sourceApplicationId;
    private String sourceApplicationStatus;
    private OffsetDateTime sourceApplicationCreatedAt;   // исправлено: OffsetDateTime

    public UserResponseDto() {}

    public UserResponseDto(Long id, String firstName, String lastName, String middleName,
                           String email, String phone, LocalDate birthDate, String status, boolean isStaff,
                           List<String> roles, List<String> permissions,
                           OffsetDateTime createdAt, OffsetDateTime updatedAt,
                           OffsetDateTime roleAssignedAt, OffsetDateTime roleCreatedAt, OffsetDateTime roleUpdatedAt,
                           Long sourceApplicationId, String sourceApplicationStatus, OffsetDateTime sourceApplicationCreatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.status = status;
        this.isStaff = isStaff;
        this.roles = roles;
        this.permissions = permissions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roleAssignedAt = roleAssignedAt;
        this.roleCreatedAt = roleCreatedAt;
        this.roleUpdatedAt = roleUpdatedAt;
        this.sourceApplicationId = sourceApplicationId;
        this.sourceApplicationStatus = sourceApplicationStatus;
        this.sourceApplicationCreatedAt = sourceApplicationCreatedAt;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isStaff() { return isStaff; }
    public void setStaff(boolean staff) { isStaff = staff; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public OffsetDateTime getRoleAssignedAt() { return roleAssignedAt; }
    public void setRoleAssignedAt(OffsetDateTime roleAssignedAt) { this.roleAssignedAt = roleAssignedAt; }
    public OffsetDateTime getRoleCreatedAt() { return roleCreatedAt; }
    public void setRoleCreatedAt(OffsetDateTime roleCreatedAt) { this.roleCreatedAt = roleCreatedAt; }
    public OffsetDateTime getRoleUpdatedAt() { return roleUpdatedAt; }
    public void setRoleUpdatedAt(OffsetDateTime roleUpdatedAt) { this.roleUpdatedAt = roleUpdatedAt; }

    public Long getSourceApplicationId() { return sourceApplicationId; }
    public void setSourceApplicationId(Long sourceApplicationId) { this.sourceApplicationId = sourceApplicationId; }
    public String getSourceApplicationStatus() { return sourceApplicationStatus; }
    public void setSourceApplicationStatus(String sourceApplicationStatus) { this.sourceApplicationStatus = sourceApplicationStatus; }
    public OffsetDateTime getSourceApplicationCreatedAt() { return sourceApplicationCreatedAt; }
    public void setSourceApplicationCreatedAt(OffsetDateTime sourceApplicationCreatedAt) { this.sourceApplicationCreatedAt = sourceApplicationCreatedAt; }
}