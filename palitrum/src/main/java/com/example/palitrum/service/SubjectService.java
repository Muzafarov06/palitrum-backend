package com.example.palitrum.service;

import com.example.palitrum.dto.SubjectDTO;
import com.example.palitrum.dto.SubjectResponse;
import com.example.palitrum.model.*;
import com.example.palitrum.repository.ProgramRepository;
import com.example.palitrum.repository.ProgramSubjectRepository;
import com.example.palitrum.repository.StudentProgramRepository;
import com.example.palitrum.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ProgramRepository programRepository;
    private final ProgramSubjectRepository programSubjectRepository;
    private final StudentProgramRepository studentProgramRepository; // добавлено

    @Transactional(readOnly = true)
    public List<SubjectResponse> getAll() {
        return subjectRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SubjectResponse> getFilteredSubjects(String search, Pageable pageable) {
        Page<Subject> page = subjectRepository.findAllWithFilters(search, pageable);
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsByProgram(Long programId) {
        List<ProgramSubject> programSubjects = programSubjectRepository.findByProgramId(programId);
        return programSubjects.stream()
                .map(ps -> toResponse(ps.getSubject()))
                .collect(Collectors.toList());
    }

    // НОВЫЙ МЕТОД: получаем предметы, связанные со студентом (через его программы)
    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsByStudent(Long studentId) {
        List<StudentProgram> studentPrograms = studentProgramRepository.findByStudentId(studentId);
        if (studentPrograms.isEmpty()) {
            return List.of();
        }
        List<Long> programIds = studentPrograms.stream()
                .map(StudentProgram::getProgramId)
                .distinct()
                .collect(Collectors.toList());
        List<ProgramSubject> programSubjects = programSubjectRepository.findByProgramIdIn(programIds);
        // Убираем дубликаты предметов (если один предмет входит в несколько программ)
        return programSubjects.stream()
                .map(ps -> toResponse(ps.getSubject()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics() {
        List<Object[]> results = subjectRepository.countGroupByLessonType();
        Map<String, Long> stats = new HashMap<>();
        long total = 0;
        for (Object[] row : results) {
            LessonType type = (LessonType) row[0];
            Long count = (Long) row[1];
            stats.put(type.name(), count);
            total += count;
        }
        stats.put("total", total);
        return stats;
    }

    public SubjectResponse getById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Предмет не найден: " + id));
        return toResponse(subject);
    }

    @Transactional
    public SubjectResponse create(SubjectDTO dto) {
        if (subjectRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Предмет с кодом '" + dto.getCode() + "' уже существует");
        }

        Subject subject = Subject.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .standardHoursPerWeek(dto.getStandardHoursPerWeek())
                .lessonType(LessonType.valueOf(dto.getLessonType()))
                .minGroupSize(dto.getMinGroupSize())
                .maxGroupSize(dto.getMaxGroupSize())
                .build();

        if (dto.getDefaultProgramId() != null) {
            Program program = programRepository.findById(dto.getDefaultProgramId())
                    .orElseThrow(() -> new RuntimeException("Программа не найдена: " + dto.getDefaultProgramId()));
            subject.setDefaultProgram(program);
        }

        Subject saved = subjectRepository.save(subject);
        return toResponse(saved);
    }

    @Transactional
    public SubjectResponse update(Long id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Предмет не найден: " + id));

        if (!subject.getCode().equals(dto.getCode()) && subjectRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Предмет с кодом '" + dto.getCode() + "' уже существует");
        }

        subject.setCode(dto.getCode());
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        subject.setStandardHoursPerWeek(dto.getStandardHoursPerWeek());
        subject.setLessonType(LessonType.valueOf(dto.getLessonType()));
        subject.setMinGroupSize(dto.getMinGroupSize());
        subject.setMaxGroupSize(dto.getMaxGroupSize());

        if (dto.getDefaultProgramId() != null) {
            Program program = programRepository.findById(dto.getDefaultProgramId())
                    .orElseThrow(() -> new RuntimeException("Программа не найдена: " + dto.getDefaultProgramId()));
            subject.setDefaultProgram(program);
        } else {
            subject.setDefaultProgram(null);
        }

        Subject updated = subjectRepository.save(subject);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Предмет не найден: " + id);
        }
        subjectRepository.deleteById(id);
    }

    private SubjectResponse toResponse(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getCode(),
                subject.getName(),
                subject.getDescription(),
                subject.getStandardHoursPerWeek(),
                subject.getDefaultProgram() != null ? subject.getDefaultProgram().getId() : null,
                subject.getLessonType().name(),
                subject.getMinGroupSize(),
                subject.getMaxGroupSize()
        );
    }
}