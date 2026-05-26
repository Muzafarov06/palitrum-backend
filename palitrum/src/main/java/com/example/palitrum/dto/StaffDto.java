// ==================== StaffDto ====================
package com.example.palitrum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StaffDto {

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotNull(message = "ID должности обязателен")
    private Long positionId;

    @NotNull(message = "Количество ставок обязательно")
    @Positive(message = "Количество ставок должно быть положительным")
    private BigDecimal rateCount;

    @NotNull(message = "Дата приёма обязательна")
    private LocalDate hireDate;

    private LocalDate dismissalDate;

    private Boolean isActive;
}