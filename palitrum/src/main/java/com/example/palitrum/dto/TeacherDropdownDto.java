package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDropdownDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Long staffId;
    private String staffPosition;   // опционально: название должности
    private java.math.BigDecimal staffRate; // опционально: количество ставок
}