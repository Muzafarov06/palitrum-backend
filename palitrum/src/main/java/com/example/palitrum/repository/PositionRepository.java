package com.example.palitrum.repository;

import com.example.palitrum.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByIsTeachingTrue();
    Optional<Position> findByName(String name);  // ← ДОБАВЬТЕ ЭТУ СТРОКУ
}