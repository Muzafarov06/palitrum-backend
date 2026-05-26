package com.example.palitrum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubjectDTO {

    @NotBlank(message = "Код обязателен")
    @Size(max = 50, message = "Код не должен превышать 50 символов")
    private String code;

    @NotBlank(message = "Название обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    private String description;

    @NotNull(message = "Часы в неделю обязательны")
    @Min(value = 0, message = "Часы не могут быть отрицательными")
    private Integer standardHoursPerWeek;

    private Long defaultProgramId;

    @NotNull(message = "Тип занятия обязателен")
    private String lessonType; // GROUP или INDIVIDUAL

    private Integer minGroupSize;
    private Integer maxGroupSize;
}