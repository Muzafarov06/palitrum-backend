package com.example.palitrum.config;

import com.example.palitrum.security.JwtFilter;
import com.example.palitrum.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты (доступ без аутентификации)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/applications").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/applications/create-with-files").permitAll()

                        // Публичные GET эндпоинты для главной страницы
                        .requestMatchers(HttpMethod.GET, "/api/news").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/news/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/programs/public").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/programs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/programs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms/filter").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/teachers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/dropdown/teachers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/settings/public").permitAll()

                        // ✅ Отделения - публичный доступ
                        .requestMatchers(HttpMethod.GET, "/api/departments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/departments/**").permitAll()

                        // Все файлы публичные для GET запросов
                        .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()

                        // Статические ресурсы
                        .requestMatchers("/", "/index.html", "/static/**", "/favicon.ico").permitAll()

                        // Всё остальное требует аутентификации
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println("{ \"error\": \"Unauthorized\" }");
        };
    }
}