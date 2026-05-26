package com.example.palitrum.repository;

import com.example.palitrum.model.ProgramSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProgramSubjectRepository extends JpaRepository<ProgramSubject, Long> {

    // Поиск по ID программы (унифицированное имя метода)
    List<ProgramSubject> findByProgramId(Long programId);

    boolean existsByProgramIdAndSubjectId(Long programId, Long subjectId);
    // Поиск по ID программы и году обучения
    List<ProgramSubject> findByProgramIdAndAcademicYear(Long programId, Integer academicYear);

    // Поиск по списку ID программ – используется для получения предметов студента
    @Query("SELECT ps FROM ProgramSubject ps WHERE ps.program.id IN :programIds")
    List<ProgramSubject> findByProgramIdIn(@Param("programIds") List<Long> programIds);

    // Проверка существования связи
    boolean existsByProgram_IdAndSubject_IdAndAcademicYear(Long programId, Long subjectId, Integer academicYear);

    // Поиск конкретной связи
    Optional<ProgramSubject> findByProgram_IdAndSubject_IdAndAcademicYear(Long programId, Long subjectId, Integer academicYear);

    // Удаление связи по трём параметрам
    @Transactional
    void deleteByProgram_IdAndSubject_IdAndAcademicYear(Long programId, Long subjectId, Integer academicYear);
}