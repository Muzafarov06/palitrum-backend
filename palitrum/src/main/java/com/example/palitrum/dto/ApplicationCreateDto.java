package com.example.palitrum.dto;

import com.example.palitrum.model.Application.ParentRelation;
import com.example.palitrum.model.Application.Source;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ApplicationCreateDto(

        // ---------- Данные ребёнка ----------
        @NotBlank(message = "Фамилия ребёнка обязательна")
        @Size(min = 2, max = 100, message = "Фамилия ребёнка должна быть от 2 до 100 символов")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]+$",
                message = "Фамилия ребёнка может содержать только буквы, дефис, пробел и апостроф")
        String childLastName,

        @NotBlank(message = "Имя ребёнка обязательно")
        @Size(min = 2, max = 100, message = "Имя ребёнка должно быть от 2 до 100 символов")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]+$",
                message = "Имя ребёнка может содержать только буквы, дефис, пробел и апостроф")
        String childFirstName,

        @Size(max = 100, message = "Отчество ребёнка слишком длинное")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]*$",
                message = "Отчество ребёнка может содержать только буквы, дефис, пробел и апостроф")
        String childMiddleName,

        @NotNull(message = "Дата рождения ребёнка обязательна")
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate childBirthDate,

        @Size(max = 255, message = "Место рождения слишком длинное")
        String childBirthPlace,                      // новое поле

        @Size(max = 100, message = "Гражданство слишком длинное")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]*$",
                message = "Гражданство может содержать только буквы, дефис, пробел и апостроф")
        String childCitizenship,

        @Size(max = 255, message = "Адрес слишком длинный")
        String childAddress,

        @Size(min = 11, max = 14, message = "СНИЛС должен содержать 11 цифр (формат XXX-XXX-XXX XX)")
        String childSnils,                           // новое поле – СНИЛС

        Boolean childIndividualPlan,                 // новое поле (по умолчанию false)

        @Size(max = 200, message = "Название предыдущей школы слишком длинное")
        String childLastSchool,                      // новое поле

        @Size(max = 20, message = "Класс слишком длинный")
        String childGradeLevel,                      // новое поле

        // ---------- Данные родителя ----------
        @NotBlank(message = "Фамилия родителя обязательна")
        @Size(min = 2, max = 100, message = "Фамилия родителя должна быть от 2 до 100 символов")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]+$",
                message = "Фамилия родителя может содержать только буквы, дефис, пробел и апостроф")
        String parentLastName,

        @NotBlank(message = "Имя родителя обязательно")
        @Size(min = 2, max = 100, message = "Имя родителя должно быть от 2 до 100 символов")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]+$",
                message = "Имя родителя может содержать только буквы, дефис, пробел и апостроф")
        String parentFirstName,

        @Size(max = 100, message = "Отчество родителя слишком длинное")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]*$",
                message = "Отчество родителя может содержать только буквы, дефис, пробел и апостроф")
        String parentMiddleName,

        @NotNull(message = "Необходимо указать кем является родитель")
        ParentRelation parentRelation,

        @NotBlank(message = "Телефон родителя обязателен")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$",
                message = "Телефон должен содержать только цифры и может начинаться с '+'")
        String parentPhone,

        @NotBlank(message = "Email родителя обязателен")
        @Email(message = "Некорректный email")
        @Size(max = 150, message = "Email слишком длинный")
        String parentEmail,

        // ---------- Программы ----------
        Long preferredProgramId,
        Long finalProgramId,

        // ---------- Согласия ----------
        @NotNull(message = "Необходимо согласие на обработку персональных данных")
        Boolean consentPersonalData,                 // новое поле – обязательно

        Boolean consentPhotoVideo,                   // новое поле (опционально, по умолчанию false)
        Boolean consentMedicalIntervention,          // новое поле (опционально, по умолчанию false)

        // ---------- Дополнительная информация (от пользователя) ----------
        @Size(max = 2000, message = "Дополнительная информация слишком длинная")
        String additionalInfo,                       // поле для свободного текста от родителя

        // ---------- Источник ----------
        Source source                                // откуда поступила заявка (SITE, PHONE, MANUAL, EMAIL)
) {}