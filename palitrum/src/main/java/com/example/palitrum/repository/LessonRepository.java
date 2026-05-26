package com.example.palitrum.repository;

import com.example.palitrum.model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByDate(LocalDate date);
    List<Lesson> findByGroupId(Long groupId);
    List<Lesson> findByTeacherId(Long teacherId);
    List<Lesson> findByRoomId(Long roomId);
    List<Lesson> findByDateAndGroupId(LocalDate date, Long groupId);
    List<Lesson> findByDateAndTeacherId(LocalDate date, Long teacherId);
    List<Lesson> findByDateAndRoomId(LocalDate date, Long roomId);
    Page<Lesson> findByDate(LocalDate date, Pageable pageable);
    Page<Lesson> findByTeacherId(Long teacherId, Pageable pageable);
    List<Lesson> findByStudentId(Long studentId);
    Page<Lesson> findByStudentId(Long studentId, Pageable pageable);
    List<Lesson> findByDateAndStudentId(LocalDate date, Long studentId);

    // Проверка конфликта для преподавателя
    @Query("SELECT COUNT(l) > 0 FROM Lesson l WHERE l.teacherId = :teacherId AND l.date = :date AND " +
            "((l.startTime < :endTime AND l.endTime > :startTime)) AND " +
            "(:excludeId IS NULL OR l.id != :excludeId)")
    boolean existsConflictForTeacher(@Param("teacherId") Long teacherId,
                                     @Param("date") LocalDate date,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime,
                                     @Param("excludeId") Long excludeId);

    // Проверка конфликта для группы
    @Query("SELECT COUNT(l) > 0 FROM Lesson l WHERE l.groupId = :groupId AND l.date = :date AND " +
            "((l.startTime < :endTime AND l.endTime > :startTime)) AND " +
            "(:excludeId IS NULL OR l.id != :excludeId)")
    boolean existsConflictForGroup(@Param("groupId") Long groupId,
                                   @Param("date") LocalDate date,
                                   @Param("startTime") LocalTime startTime,
                                   @Param("endTime") LocalTime endTime,
                                   @Param("excludeId") Long excludeId);

    List<Lesson> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Lesson> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);
    List<Lesson> findByTeacherIdAndDateBetween(Long teacherId, LocalDate startDate, LocalDate endDate);
    List<Lesson> findByGroupIdAndDateBetween(Long groupId, LocalDate startDate, LocalDate endDate);

    // Поиск занятий по списку ID групп за период
    List<Lesson> findByGroupIdInAndDateBetween(List<Long> groupIds, LocalDate startDate, LocalDate endDate);

    // Проверка конфликта для аудитории
    @Query("SELECT COUNT(l) > 0 FROM Lesson l WHERE l.roomId = :roomId AND l.date = :date AND " +
            "((l.startTime < :endTime AND l.endTime > :startTime)) AND " +
            "(:excludeId IS NULL OR l.id != :excludeId)")
    boolean existsConflictForRoom(@Param("roomId") Long roomId,
                                  @Param("date") LocalDate date,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime,
                                  @Param("excludeId") Long excludeId);

    // Проверка конфликта для студента
    @Query("SELECT COUNT(l) > 0 FROM Lesson l WHERE l.studentId = :studentId AND l.date = :date AND " +
            "((l.startTime < :endTime AND l.endTime > :startTime)) AND " +
            "(:excludeId IS NULL OR l.id != :excludeId)")
    boolean existsConflictForStudent(@Param("studentId") Long studentId,
                                     @Param("date") LocalDate date,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime,
                                     @Param("excludeId") Long excludeId);

    @Query("SELECT l FROM Lesson l WHERE l.studentId = :studentId AND l.subjectId = :subjectId AND l.date BETWEEN :start AND :end")
    List<Lesson> findByStudentIdAndSubjectIdAndDateBetween(@Param("studentId") Long studentId,
                                                           @Param("subjectId") Long subjectId,
                                                           @Param("start") LocalDate start,
                                                           @Param("end") LocalDate end);

    @Query("SELECT l FROM Lesson l WHERE l.groupId IN :groupIds AND l.subjectId = :subjectId AND l.date BETWEEN :start AND :end")
    List<Lesson> findByGroupIdInAndSubjectIdAndDateBetween(@Param("groupIds") List<Long> groupIds,
                                                           @Param("subjectId") Long subjectId,
                                                           @Param("start") LocalDate start,
                                                           @Param("end") LocalDate end);
}