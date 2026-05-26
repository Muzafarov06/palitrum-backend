package com.example.palitrum.repository;

import com.example.palitrum.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    Page<Role> findByNameContainingIgnoreCase(String name, Pageable pageable);
}