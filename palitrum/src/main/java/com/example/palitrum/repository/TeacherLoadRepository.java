package com.example.palitrum.repository;

import com.example.palitrum.model.TeacherLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherLoadRepository extends JpaRepository<TeacherLoad, Long> {
    Optional<TeacherLoad> findByTeacherIdAndAcademicPeriodId(Long teacherId, Long academicPeriodId);
}