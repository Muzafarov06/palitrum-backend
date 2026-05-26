package com.example.palitrum.repository;

import com.example.palitrum.model.StudentProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentProgramRepository extends JpaRepository<StudentProgram, Long> {
    List<StudentProgram> findByStudentId(Long studentId);

    List<StudentProgram> findByStudentIdAndStatus(Long studentId, StudentProgram.Status status);
}