// ==================== StaffResponse ====================
package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long positionId;
    private String positionName;
    private BigDecimal rateCount;
    private BigDecimal hoursPerRate;
    private BigDecimal maxHoursPerWeek;
    private LocalDate hireDate;
    private LocalDate dismissalDate;
    private Boolean isActive;
}