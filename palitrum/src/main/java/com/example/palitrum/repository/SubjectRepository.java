package com.example.palitrum.repository;

import com.example.palitrum.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByCode(String code);
    boolean existsByCode(String code);

    // Фильтрация с пагинацией: поиск по названию или коду (регистронезависимо)
    @Query("SELECT s FROM Subject s WHERE " +
            "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')))")
    Page<Subject> findAllWithFilters(@Param("search") String search, Pageable pageable);

    // Статистика: группировка по типу занятий
    @Query("SELECT s.lessonType, COUNT(s) FROM Subject s GROUP BY s.lessonType")
    List<Object[]> countGroupByLessonType();
}