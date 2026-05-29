package com.example.palitrum.security;

import com.example.palitrum.model.User;
import com.example.palitrum.repository.UserRepository;
import com.example.palitrum.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    /**
     * Определяет, нужно ли пропускать фильтр для данного запроса.
     * Возвращает true для публичных эндпоинтов, которые не требуют проверки токена.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Список публичных путей, которые не требуют проверки токена
        return path.startsWith("/auth/login") ||
                path.startsWith("/api/programs/public") ||
                path.startsWith("/api/rooms") ||
                path.startsWith("/api/news") ||
                path.startsWith("/api/settings/public") ||
                path.startsWith("/api/files/") ||
                path.equals("/") ||
                path.startsWith("/index.html") ||
                path.startsWith("/static/") ||
                path.startsWith("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        System.out.println("🔍 Authorization header: " + auth);

        if (auth == null || !auth.startsWith("Bearer ")) {
            System.out.println("❌ Нет Bearer токена, пропускаем фильтр");
            chain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);
        System.out.println("🔍 Токен: " + token);

        Claims claims;
        try {
            claims = jwtUtil.extractClaims(token);
            System.out.println("✅ Claims извлечены: " + claims);
        } catch (JwtException e) {
            System.out.println("❌ Ошибка JWT: " + e.getMessage());
            chain.doFilter(request, response);
            return;
        }

        String email = claims.get("email", String.class);
        System.out.println("🔍 Email из токена: " + email);

        if (email == null) {
            System.out.println("❌ Email отсутствует в токене");
            chain.doFilter(request, response);
            return;
        }

        var userDetails = userDetailsService.loadUserByUsername(email);
        System.out.println("✅ UserDetails загружен: " + userDetails.getUsername());

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("✅ Аутентификация установлена в SecurityContext");

        User entity = userRepository.findByEmail(email).orElse(null);
        if (entity != null) {
            request.setAttribute("user", entity);
        }

        chain.doFilter(request, response);
    }
}