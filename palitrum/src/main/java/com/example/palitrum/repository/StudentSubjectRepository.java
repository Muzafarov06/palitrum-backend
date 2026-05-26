package com.example.palitrum.repository;

import com.example.palitrum.model.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    // Нативный SQL – возвращает ID студентов, у которых есть индивидуальный предмет без назначенного преподавателя
    @Query(value = "SELECT DISTINCT sp.student_id FROM student_subject ss " +
            "JOIN student_program sp ON ss.student_program_id = sp.id " +
            "WHERE ss.is_group_lesson = false AND ss.teacher_id IS NULL",
            nativeQuery = true)
    List<Long> findStudentIdsWithIndividualNoTeacher();
}