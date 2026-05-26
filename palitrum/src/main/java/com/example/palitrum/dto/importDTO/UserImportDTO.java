// src/main/java/com/example/palitrum/dto/importDTO/UserImportDTO.java
package com.example.palitrum.dto.importDTO;

import java.time.LocalDate;

public record UserImportDTO(
        String firstName,
        String lastName,
        String middleName,
        String email,
        String phone,
        LocalDate birthDate,
        String status,          // ACTIVE, PENDING, BLOCKED, ARCHIVED
        Boolean isStaff,
        String roleName,        // STUDENT, TEACHER, PARENT, MANAGER, ADMIN
        String parentEmail      // только для STUDENT
) {}