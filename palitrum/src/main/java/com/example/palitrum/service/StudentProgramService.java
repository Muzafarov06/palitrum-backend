package com.example.palitrum.service;

import com.example.palitrum.dto.StudentEnrollmentDto;
import com.example.palitrum.dto.StudentProgramResponse;
import com.example.palitrum.model.StudentProgram;
import com.example.palitrum.model.User;
import com.example.palitrum.model.Program;
import com.example.palitrum.repository.StudentProgramRepository;
import com.example.palitrum.repository.UserRepository;
import com.example.palitrum.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentProgramService {

    private final StudentProgramRepository repository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;

    @Transactional(readOnly = true)
    public List<StudentProgramResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StudentProgramResponse create(StudentEnrollmentDto dto) {
        if (!userRepository.existsById(dto.getStudentId())) {
            throw new IllegalArgumentException("Студент не найден");
        }
        if (!programRepository.existsById(dto.getProgramId())) {
            throw new IllegalArgumentException("Программа не найдена");
        }

        StudentProgram sp = StudentProgram.builder()
                .studentId(dto.getStudentId())
                .programId(dto.getProgramId())
                .enrollmentDate(dto.getEnrollmentDate())
                .status(StudentProgram.Status.ENROLLED)
                .build();
        return toResponse(repository.save(sp));
    }

    public StudentProgramResponse update(Long id, StudentEnrollmentDto dto) {
        StudentProgram sp = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));

        if (dto.getStatus() != null) {
            sp.setStatus(StudentProgram.Status.valueOf(dto.getStatus()));
        }
        if (dto.getEnrollmentDate() != null) {
            sp.setEnrollmentDate(dto.getEnrollmentDate());
        }
        if (dto.getGraduationDate() != null) {
            sp.setGraduationDate(dto.getGraduationDate());
        }

        return toResponse(repository.save(sp));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private StudentProgramResponse toResponse(StudentProgram sp) {
        User student = userRepository.findById(sp.getStudentId()).orElse(null);
        Program program = programRepository.findById(sp.getProgramId()).orElse(null);

        return StudentProgramResponse.builder()
                .id(sp.getId())
                .studentId(sp.getStudentId())
                .studentName(student != null ? student.getFullName() : "—")
                .programId(sp.getProgramId())
                .programName(program != null ? program.getName() : "—")
                .enrollmentDate(sp.getEnrollmentDate())
                .graduationDate(sp.getGraduationDate())
                .status(sp.getStatus().name())
                .build();
    }
}