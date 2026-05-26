// com.example.palitrum.repository.LessonParticipantRepository.java
package com.example.palitrum.repository;

import com.example.palitrum.model.LessonParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonParticipantRepository extends JpaRepository<LessonParticipant, Long> {

    // Найти всех участников одного урока
    List<LessonParticipant> findByLessonId(Long lessonId);

    // Для преподавателя: его уроки за период с участниками
    @Query("SELECT lp FROM LessonParticipant lp WHERE lp.lessonId IN " +
            "(SELECT l.id FROM Lesson l WHERE l.teacherId = :teacherId AND l.date BETWEEN :from AND :to)")
    List<LessonParticipant> findByTeacherAndPeriod(@Param("teacherId") Long teacherId,
                                                   @Param("from") LocalDate from,
                                                   @Param("to") LocalDate to);

    // LessonParticipantRepository.java
    List<LessonParticipant> findByUserId(Long userId);

    Optional<LessonParticipant> findByLessonIdAndUserId(Long lessonId, Long userId);
    // Для студента: его уроки за период с участниками
    @Query("SELECT lp FROM LessonParticipant lp WHERE lp.userId = :studentId AND lp.lessonId IN " +
            "(SELECT l.id FROM Lesson l WHERE l.date BETWEEN :from AND :to)")
    List<LessonParticipant> findByStudentAndPeriod(@Param("studentId") Long studentId,
                                                   @Param("from") LocalDate from,
                                                   @Param("to") LocalDate to);

    // Проверка уникальности (lesson_id, user_id) гарантируется на уровне БД
    boolean existsByLessonIdAndUserId(Long lessonId, Long userId);
}