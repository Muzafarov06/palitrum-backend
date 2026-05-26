package com.example.palitrum.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.palitrum.model.SystemSettings;
import com.example.palitrum.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> getSettings() {
        SystemSettings entity = repository.findById(1).orElse(null);
        if (entity == null) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(entity.getSettings(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Transactional
    public Map<String, Object> updateSettings(Map<String, Object> newSettings) {
        try {
            String json = objectMapper.writeValueAsString(newSettings);
            SystemSettings entity = repository.findById(1)
                    .orElse(new SystemSettings());
            entity.setSettings(json);
            entity.setUpdatedAt(LocalDateTime.now());
            repository.save(entity);
            return newSettings;
        } catch (Exception e) {
            throw new RuntimeException("Error saving settings", e);
        }
    }
}