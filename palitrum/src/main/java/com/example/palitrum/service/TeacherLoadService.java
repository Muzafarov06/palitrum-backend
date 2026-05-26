package com.example.palitrum.service;

import com.example.palitrum.dto.TeacherLoadDto;
import com.example.palitrum.dto.TeacherLoadResponse;
import com.example.palitrum.model.*;
import com.example.palitrum.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherLoadService {

    private final TeacherLoadRepository repository;
    private final UserRepository userRepository;
    private final AcademicPeriodRepository periodRepository;
    private final StaffRepository staffRepository;
    private final PositionRepository positionRepository;
    private final StudentSubjectRepository studentSubjectRepository;   // новое

    @Transactional(readOnly = true)
    public List<TeacherLoadResponse> getAll(Long periodId) {
        List<TeacherLoad> loads;
        if (periodId != null) {
            loads = repository.findAll().stream()
                    .filter(l -> l.getAcademicPeriodId().equals(periodId))
                    .collect(Collectors.toList());
        } else {
            loads = repository.findAll();
        }
        return loads.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TeacherLoadResponse create(TeacherLoadDto dto) {
        if (!userRepository.existsById(dto.getTeacherId())) {
            throw new IllegalArgumentException("Преподаватель не найден");
        }
        if (!periodRepository.existsById(dto.getAcademicPeriodId())) {
            throw new IllegalArgumentException("Учебный период не найден");
        }
        if (repository.findByTeacherIdAndAcademicPeriodId(dto.getTeacherId(), dto.getAcademicPeriodId()).isPresent()) {
            throw new IllegalArgumentException("Нагрузка для этого преподавателя на данный период уже существует");
        }

        TeacherLoad load = TeacherLoad.builder()
                .teacherId(dto.getTeacherId())
                .academicPeriodId(dto.getAcademicPeriodId())
                .staffId(dto.getStaffId())
                .weeklyHoursPlanned(dto.getWeeklyHoursPlanned())
                .maxWeeklyHours(dto.getMaxWeeklyHours())
                .build();
        return toResponse(repository.save(load));
    }

    public TeacherLoadResponse update(Long id, TeacherLoadDto dto) {
        TeacherLoad load = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));
        load.setWeeklyHoursPlanned(dto.getWeeklyHoursPlanned());
        load.setMaxWeeklyHours(dto.getMaxWeeklyHours());
        return toResponse(repository.save(load));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ========== АВТОМАТИЧЕСКИЙ ПЕРЕСЧЁТ ПЛАНОВОЙ НАГРУЗКИ ==========
    /**
     * Пересчитывает weekly_hours_planned для преподавателя на основе student_subject.
     * Суммирует planned_hours_per_week по всем предметам, которые ведёт преподаватель,
     * независимо от периода (можно доработать фильтр по периоду через группы).
     */
    @Transactional
    public TeacherLoadResponse recalculatePlannedHours(Long teacherLoadId) {
        TeacherLoad load = repository.findById(teacherLoadId)
                .orElseThrow(() -> new IllegalArgumentException("Запись нагрузки не найдена"));

        // Получаем все student_subject, где teacher_id = load.teacherId
        List<StudentSubject> subjects = studentSubjectRepository.findAll().stream()
                .filter(ss -> ss.getTeacherId() != null && ss.getTeacherId().equals(load.getTeacherId()))
                .collect(Collectors.toList());

        BigDecimal totalPlanned = subjects.stream()
                .map(ss -> ss.getPlannedHoursPerWeek() != null ? ss.getPlannedHoursPerWeek() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        load.setWeeklyHoursPlanned(totalPlanned);
        repository.save(load);
        return toResponse(load);
    }

    /**
     * Пересчитывает нагрузку для всех преподавателей за указанный период.
     */
    @Transactional
    public Map<String, Object> recalculateAllForPeriod(Long periodId) {
        List<TeacherLoad> loads = repository.findAll().stream()
                .filter(l -> l.getAcademicPeriodId().equals(periodId))
                .collect(Collectors.toList());

        int updated = 0;
        for (TeacherLoad load : loads) {
            List<StudentSubject> subjects = studentSubjectRepository.findAll().stream()
                    .filter(ss -> ss.getTeacherId() != null && ss.getTeacherId().equals(load.getTeacherId()))
                    .collect(Collectors.toList());

            BigDecimal totalPlanned = subjects.stream()
                    .map(ss -> ss.getPlannedHoursPerWeek() != null ? ss.getPlannedHoursPerWeek() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            load.setWeeklyHoursPlanned(totalPlanned);
            repository.save(load);
            updated++;
        }

        return Map.of("updated", updated);
    }

    // TeacherLoadService.java – добавить метод
    @Transactional
    public Map<String, Object> generateMissingLoads(Long periodId) {
        if (!periodRepository.existsById(periodId)) {
            throw new IllegalArgumentException("Период не найден");
        }

        // Получаем всех активных преподавателей из штатного расписания
        List<Staff> activeStaff = staffRepository.findAll().stream()
                .filter(Staff::getIsActive)
                .collect(Collectors.toList());

        int created = 0;
        int skipped = 0;

        for (Staff staff : activeStaff) {
            // Проверяем, есть ли уже нагрузка для этого преподавателя на этот период
            if (repository.findByTeacherIdAndAcademicPeriodId(staff.getUserId(), periodId).isPresent()) {
                skipped++;
                continue;
            }

            Position position = positionRepository.findById(staff.getPositionId()).orElse(null);
            if (position == null || !position.getIsTeaching()) {
                skipped++;
                continue;
            }

            // Рассчитываем максимальные часы (ставка × норма должности)
            BigDecimal maxHours = position.getHoursPerRate().multiply(staff.getRateCount());

            // *** НОВОЕ: сразу считаем фактическую нагрузку из student_subject ***
            BigDecimal actualPlanned = calculatePlannedHours(staff.getUserId());

            TeacherLoad load = TeacherLoad.builder()
                    .teacherId(staff.getUserId())
                    .academicPeriodId(periodId)
                    .staffId(staff.getId())
                    .weeklyHoursPlanned(actualPlanned)  // реальные часы из учебного плана
                    .maxWeeklyHours(maxHours)
                    .build();
            repository.save(load);
            created++;
        }

        return Map.of("created", created, "skipped", skipped);
    }

    // Вспомогательный метод: сумма planned_hours_per_week для преподавателя
    private BigDecimal calculatePlannedHours(Long teacherId) {
        List<StudentSubject> subjects = studentSubjectRepository.findAll().stream()
                .filter(ss -> ss.getTeacherId() != null && ss.getTeacherId().equals(teacherId))
                .collect(Collectors.toList());

        return subjects.stream()
                .map(ss -> ss.getPlannedHoursPerWeek() != null ? ss.getPlannedHoursPerWeek() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private TeacherLoadResponse toResponse(TeacherLoad load) {
        User teacher = userRepository.findById(load.getTeacherId()).orElse(null);
        AcademicPeriod period = periodRepository.findById(load.getAcademicPeriodId()).orElse(null);

        String positionName = null;
        BigDecimal rateCount = null;
        if (load.getStaffId() != null) {
            Staff staff = staffRepository.findById(load.getStaffId()).orElse(null);
            if (staff != null) {
                rateCount = staff.getRateCount();
                Position position = positionRepository.findById(staff.getPositionId()).orElse(null);
                if (position != null) {
                    positionName = position.getName();
                }
            }
        }

        return TeacherLoadResponse.builder()
                .id(load.getId())
                .teacherId(load.getTeacherId())
                .teacherName(teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "—")
                .academicPeriodId(load.getAcademicPeriodId())
                .periodName(period != null ? period.getName() : "—")
                .staffId(load.getStaffId())
                .positionName(positionName)
                .rateCount(rateCount)
                .weeklyHoursPlanned(load.getWeeklyHoursPlanned())
                .maxWeeklyHours(load.getMaxWeeklyHours())
                .build();
    }
}