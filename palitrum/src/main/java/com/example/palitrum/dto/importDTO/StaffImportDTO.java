package com.example.palitrum.dto.importDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StaffImportDTO(
        String userEmail,      // email пользователя (сотрудника)
        String positionName,   // название должности
        BigDecimal rateCount,  // количество ставок (например, 1.0, 0.5)
        LocalDate hireDate,    // дата приёма
        Boolean isActive       // активен (true) / уволен (false) – опционально
) {}