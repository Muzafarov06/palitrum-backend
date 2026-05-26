package com.example.palitrum.repository;

import com.example.palitrum.model.ScheduleTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, Long> {

    // Методы проверки конфликтов временно удалены (заменены на логику в сервисе)
    Page<ScheduleTemplate> findByAcademicPeriodId(Long periodId, Pageable pageable);
    Page<ScheduleTemplate> findByGroupId(Long groupId, Pageable pageable);
    Page<ScheduleTemplate> findByStudentId(Long studentId, Pageable pageable);
    Page<ScheduleTemplate> findByTeacherId(Long teacherId, Pageable pageable);
}