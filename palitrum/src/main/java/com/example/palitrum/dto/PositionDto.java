// ====================== DTO ======================
package com.example.palitrum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PositionDto {
    @NotBlank(message = "Название должности обязательно")
    private String name;

    @NotNull(message = "Часы на ставку обязательны")
    @Positive(message = "Часы должны быть положительными")
    private BigDecimal hoursPerRate;

    private Boolean isTeaching;
}
