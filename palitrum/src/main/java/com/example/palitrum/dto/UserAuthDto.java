package com.example.palitrum.dto;

import java.util.List;

public record UserAuthDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        List<String> roles,
        List<String> permissions
) {}
