package com.example.palitrum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserCreateDto(
        @NotBlank(message = "Имя обязательно")
        @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]+$", message = "Имя может содержать только буквы, дефис, пробел и апостроф")
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]+$", message = "Фамилия может содержать только буквы, дефис, пробел и апостроф")
        String lastName,

        @Size(max = 50, message = "Отчество не должно быть длиннее 50 символов")
        String middleName,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        @Size(max = 100, message = "Email слишком длинный")
        String email,

        @NotBlank(message = "Телефон обязателен")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Телефон должен содержать только цифры и может начинаться с '+'"
        )
        String phone,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
        String password,

        @NotNull(message = "Дата рождения обязательна")
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate,

        boolean isStaff
) {}