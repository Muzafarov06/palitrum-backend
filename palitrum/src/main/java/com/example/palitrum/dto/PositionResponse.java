// PositionResponse
package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class PositionResponse {
    private Long id;
    private String name;
    private BigDecimal hoursPerRate;
    private Boolean isTeaching;
}