package com.example.palitrum.repository;

import com.example.palitrum.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByProgramIdAndAcademicYearAndSubjectId(Long programId, Integer academicYear, Long subjectId);

    @Query("SELECT g FROM Group g WHERE g.academicYear = :academicYear")
    List<Group> findByAcademicYear(@Param("academicYear") Integer academicYear);

    List<Group> findByStatus(String status);

    List<Group> findByProgramIdAndAcademicYear(Long programId, Integer academicYear);

    // Новый метод: получить ID всех групп, принадлежащих программе
    @Query("SELECT g.id FROM Group g WHERE g.programId = :programId")
    List<Long> findGroupIdsByProgramId(@Param("programId") Long programId);
}