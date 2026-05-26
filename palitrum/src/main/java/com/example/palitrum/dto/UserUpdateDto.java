package com.example.palitrum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UserUpdateDto(
        @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]+$", message = "Имя может содержать только буквы, дефис, пробел и апостроф")
        String firstName,

        @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]+$", message = "Фамилия может содержать только буквы, дефис, пробел и апостроф")
        String lastName,

        @Size(max = 50, message = "Отчество слишком длинное")
        @Pattern(regexp = "^[\\p{L}\\p{M}'\\-\\s]*$", message = "Отчество может содержать только буквы, дефис, пробел и апостроф")
        String middleName,

        @Email(message = "Некорректный email")
        @Size(max = 100, message = "Email слишком длинный")
        String email,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Телефон должен содержать только цифры и может начинаться с '+'")
        String phone,

        @Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
        String password,

        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate,

        Boolean isStaff,

        String status
) {}