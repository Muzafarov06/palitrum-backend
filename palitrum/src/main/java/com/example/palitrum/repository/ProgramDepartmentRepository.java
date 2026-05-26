package com.example.palitrum.repository;

import com.example.palitrum.model.ProgramDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProgramDepartmentRepository extends JpaRepository<ProgramDepartment, Long> {

    // 1. Проверка существования - Spring Data JPA сам сгенерирует реализацию
    boolean existsByProgramIdAndDepartmentId(Long programId, Long departmentId);

    // 2. Удаление по ID программы и отделения
    @Transactional
    void deleteByProgramIdAndDepartmentId(Long programId, Long departmentId);

    // 3. Поиск всех связей по отделению
    List<ProgramDepartment> findByDepartmentId(Long departmentId);

    // 4. Поиск всех связей по программе
    List<ProgramDepartment> findByProgramId(Long programId);

    // 5. Опциональный поиск конкретной связи
    Optional<ProgramDepartment> findByProgramIdAndDepartmentId(Long programId, Long departmentId);
}