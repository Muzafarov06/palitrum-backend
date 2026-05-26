package com.example.palitrum.controller;

import com.example.palitrum.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SystemSettingsController {

    private final SystemSettingsService service;

    // Публичный эндпоинт – доступен всем для получения общей информации
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getPublicSettings() {
        Map<String, Object> allSettings = service.getSettings();
        Map<String, Object> general = (Map<String, Object>) allSettings.getOrDefault("general", new HashMap<>());

        Map<String, Object> publicInfo = new HashMap<>();
        publicInfo.put("orgName", general.get("orgName"));
        publicInfo.put("email", general.get("email"));
        publicInfo.put("phone", general.get("phone"));
        publicInfo.put("address", general.get("address"));
        publicInfo.put("timezone", general.get("timezone"));
        publicInfo.put("language", general.get("language"));
        publicInfo.put("currency", general.get("currency"));
        publicInfo.put("logoUrl", general.get("logoUrl"));

        return ResponseEntity.ok(publicInfo);
    }

    // Только для SUPER_ADMIN
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSettings() {
        return ResponseEntity.ok(service.getSettings());
    }

    // Только для SUPER_ADMIN
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody Map<String, Object> settings) {
        return ResponseEntity.ok(service.updateSettings(settings));
    }
}