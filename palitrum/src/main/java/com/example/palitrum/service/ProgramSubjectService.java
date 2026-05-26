package com.example.palitrum.service;

import com.example.palitrum.dto.ProgramSubjectDTO;
import com.example.palitrum.dto.ProgramSubjectResponse;
import com.example.palitrum.model.Program;
import com.example.palitrum.model.ProgramSubject;
import com.example.palitrum.model.Subject;
import com.example.palitrum.repository.ProgramRepository;
import com.example.palitrum.repository.ProgramSubjectRepository;
import com.example.palitrum.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramSubjectService {

    private final ProgramSubjectRepository repository;
    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<ProgramSubjectResponse> getByProgram(Long programId) {
        // Исправлено: метод переименован в findByProgramId
        return repository.findByProgramId(programId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProgramSubjectResponse link(ProgramSubjectDTO dto) {
        Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new RuntimeException("Программа не найдена: " + dto.getProgramId()));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден: " + dto.getSubjectId()));

        int durationYears = program.getDurationYears() != null ? program.getDurationYears() : 4;
        if (dto.getAcademicYear() > durationYears) {
            throw new IllegalArgumentException("Год обучения не может превышать длительность программы (" + durationYears + " лет)");
        }

        if (repository.existsByProgram_IdAndSubject_IdAndAcademicYear(dto.getProgramId(), dto.getSubjectId(), dto.getAcademicYear())) {
            throw new RuntimeException("Предмет уже добавлен в программу для указанного года обучения");
        }

        ProgramSubject ps = ProgramSubject.builder()
                .program(program)
                .subject(subject)
                .academicYear(dto.getAcademicYear())
                .hoursPerWeekForProgram(dto.getHoursPerWeekForProgram() != null
                        ? dto.getHoursPerWeekForProgram().setScale(2, java.math.RoundingMode.HALF_UP)
                        : BigDecimal.ZERO)
                .build();

        ProgramSubject saved = repository.save(ps);
        return toResponse(saved);
    }

    @Transactional
    public ProgramSubjectResponse update(ProgramSubjectDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID связи не может быть null при обновлении");
        }
        ProgramSubject ps = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Связь не найдена: " + dto.getId()));

        Program program = ps.getProgram();
        int durationYears = program.getDurationYears() != null ? program.getDurationYears() : 4;
        if (dto.getAcademicYear() != null && dto.getAcademicYear() > durationYears) {
            throw new IllegalArgumentException("Год обучения не может превышать " + durationYears);
        }

        if (dto.getAcademicYear() != null) ps.setAcademicYear(dto.getAcademicYear());
        if (dto.getHoursPerWeekForProgram() != null) {
            BigDecimal hours = dto.getHoursPerWeekForProgram().setScale(2, java.math.RoundingMode.HALF_UP);
            ps.setHoursPerWeekForProgram(hours);
        }

        ProgramSubject saved = repository.save(ps);
        return toResponse(saved);
    }

    @Transactional
    public void unlink(Long programId, Long subjectId, Integer academicYear) {
        if (!repository.existsByProgram_IdAndSubject_IdAndAcademicYear(programId, subjectId, academicYear)) {
            throw new RuntimeException("Связь программы " + programId + ", предмета " + subjectId +
                    " и года " + academicYear + " не найдена");
        }
        repository.deleteByProgram_IdAndSubject_IdAndAcademicYear(programId, subjectId, academicYear);
    }

    private ProgramSubjectResponse toResponse(ProgramSubject ps) {
        return new ProgramSubjectResponse(
                ps.getId(),
                ps.getProgram().getId(),
                ps.getSubject().getId(),
                ps.getSubject().getName(),
                ps.getAcademicYear(),
                ps.getHoursPerWeekForProgram()
        );
    }
}