package com.example.palitrum.repository;

import com.example.palitrum.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByName(String name);
    boolean existsByName(String name);
}
