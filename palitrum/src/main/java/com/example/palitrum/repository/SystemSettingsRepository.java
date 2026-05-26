package com.example.palitrum.repository;

import com.example.palitrum.model.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Integer> {
}