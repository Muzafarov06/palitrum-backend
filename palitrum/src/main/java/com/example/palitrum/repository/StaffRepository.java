package com.example.palitrum.repository;

import com.example.palitrum.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUserId(Long userId);
    Optional<Staff> findByUserIdAndIsActiveTrue(Long userId);
    // ❌ удалён метод findByName – у Staff нет поля name
}