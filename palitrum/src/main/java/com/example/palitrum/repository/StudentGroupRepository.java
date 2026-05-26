package com.example.palitrum.repository;

import com.example.palitrum.model.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {

    // Старый метод, который используется в генерации уроков
    List<StudentGroup> findByGroupIdAndEnrollmentStatus(Long groupId, StudentGroup.EnrollmentStatus status);


    // Новый метод: получаем ID групп, в которых студент активно обучается
    @Query("SELECT sg.groupId FROM StudentGroup sg WHERE sg.userId = :userId AND sg.enrollmentStatus = 'ENROLLED'")
    List<Long> findActiveGroupIdsByUserId(@Param("userId") Long userId);
}