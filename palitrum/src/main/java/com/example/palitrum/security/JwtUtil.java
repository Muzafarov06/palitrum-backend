package com.example.palitrum.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtUtil {

    // В реальном приложении вынести в свойства (application.properties) и держать длинный секрет.
    private final String SECRET = "SUPER_SECRET_KEY_777_SUPER_SECRET_KEY_777"; // >= 32 байт

    // Генерация токена
    public String generateToken(Long userId, String email, List<String> roles, List<String> permissions) {
        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24); // 24 часа

        return Jwts.builder()
                .claim("id", userId)
                .claim("email", email)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    // Разбор токена и получение Claims
    public Claims extractClaims(String token) throws JwtException {
        System.out.println("🔍 Парсим токен: " + token);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("✅ Токен валиден, claims: " + claims);
            return claims;
        } catch (JwtException e) {
            System.out.println("❌ Ошибка парсинга JWT: " + e.getMessage());
            throw e;
        }
    }
}
