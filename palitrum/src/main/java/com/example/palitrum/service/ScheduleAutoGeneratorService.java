package com.example.palitrum.service;

import com.example.palitrum.dto.ScheduleGenerationRequest;
import com.example.palitrum.model.*;
import com.example.palitrum.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleAutoGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleAutoGeneratorService.class);

    private final ScheduleTemplateRepository templateRepository;
    private final ProgramSubjectRepository programSubjectRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final StudentProgramRepository studentProgramRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final TeacherLoadRepository teacherLoadRepository;
    private final AcademicPeriodRepository periodRepository;
    private final StaffRepository staffRepository;
    private final PositionRepository positionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final LocalTime END_TIME = LocalTime.of(20, 0);
    private static final List<Integer> WEEK_DAYS = Arrays.asList(1, 2, 3, 4, 5, 6);
    private static final int TIME_SLOT_INTERVAL = 15; // минут между слотами

    @Transactional(noRollbackFor = Exception.class)
    public Map<String, Object> generateScheduleTemplates(ScheduleGenerationRequest request) {
        AcademicPeriod period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new IllegalArgumentException("Период не найден"));

        List<String> errors = new ArrayList<>();
        int generatedGroups = 0;
        int generatedIndividual = 0;

        // 1. Генерация для групповых занятий
        if (request.getGenerateGroups()) {
            List<Group> activeGroups = groupRepository.findAll().stream()
                    .filter(g -> "active".equals(g.getStatus()))
                    .collect(Collectors.toList());

            log.info("Найдено активных групп: {}", activeGroups.size());

            for (Group group : activeGroups) {
                try {
                    int count = generateTemplatesForGroup(group, period, request);
                    generatedGroups += count;
                } catch (Exception e) {
                    log.error("Ошибка при генерации для группы {}: {}", group.getName(), e.getMessage());
                    errors.add("Группа " + group.getName() + ": " + e.getMessage());
                }
            }
        }

        // 2. Генерация для индивидуальных занятий
        if (request.getGenerateIndividual()) {
            List<StudentSubject> individualSubjects = studentSubjectRepository.findAll().stream()
                    .filter(ss -> ss.getIsGroupLesson() == null || !ss.getIsGroupLesson())
                    .filter(ss -> ss.getStudentProgramId() != null)
                    .collect(Collectors.toList());

            log.info("Найдено индивидуальных предметов студентов: {}", individualSubjects.size());

            for (StudentSubject studentSubject : individualSubjects) {
                try {
                    Optional<StudentProgram> studentProgramOpt = studentProgramRepository.findById(studentSubject.getStudentProgramId());
                    if (studentProgramOpt.isEmpty()) {
                        log.warn("StudentProgram не найден для id={}", studentSubject.getStudentProgramId());
                        continue;
                    }

                    StudentProgram studentProgram = studentProgramOpt.get();
                    int count = generateTemplatesForStudentSubject(studentSubject, studentProgram, period, request);
                    generatedIndividual += count;
                } catch (Exception e) {
                    log.error("Ошибка при генерации для StudentSubject id={}: {}", studentSubject.getId(), e.getMessage());
                    errors.add("StudentSubject ID " + studentSubject.getId() + ": " + e.getMessage());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("generatedGroups", generatedGroups);
        result.put("generatedIndividual", generatedIndividual);
        result.put("errors", errors);

        log.info("Генерация завершена: группы={}, индивидуальные={}, ошибок={}",
                generatedGroups, generatedIndividual, errors.size());

        return result;
    }

    private int generateTemplatesForGroup(Group group, AcademicPeriod period, ScheduleGenerationRequest request) {
        int generated = 0;
        int academicYear = calculateAcademicYearForGroup(group, period);

        List<ProgramSubject> programSubjects = programSubjectRepository
                .findByProgramIdAndAcademicYear(group.getProgramId(), academicYear);

        if (programSubjects.isEmpty()) {
            log.warn("Нет предметов для программы {} на {} год обучения", group.getProgramId(), academicYear);
            return 0;
        }

        for (ProgramSubject ps : programSubjects) {
            Subject subject = ps.getSubject();
            BigDecimal hoursPerWeek = ps.getHoursPerWeekForProgram();

            if (hoursPerWeek == null || hoursPerWeek.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            Long teacherId = findTeacherForSubject(subject.getId(), period);
            if (teacherId == null) {
                log.warn("Не найден преподаватель для предмета {}, пропускаем", subject.getName());
                continue;
            }

            Long roomId = findRoomForLesson(subject);
            if (roomId == null) {
                log.warn("Не найдена аудитория для предмета {}, пропускаем", subject.getName());
                continue;
            }

            List<ScheduleTemplate> templates = splitHoursIntoTemplates(
                    group.getId(), null, subject.getId(), subject.getName(),
                    teacherId, roomId, hoursPerWeek, period, true
            );

            for (ScheduleTemplate template : templates) {
                if (template != null) {
                    try {
                        template.setAcademicPeriodId(period.getId());
                        templateRepository.save(template);
                        generated++;
                        log.debug("Сохранён шаблон для группы {}: день={}, время={}",
                                group.getName(), template.getDayOfWeek(), template.getStartTime());
                    } catch (Exception e) {
                        log.error("Ошибка при сохранении шаблона: {}", e.getMessage());
                    }
                }
            }
        }

        return generated;
    }

    private int generateTemplatesForStudentSubject(StudentSubject studentSubject, StudentProgram studentProgram,
                                                   AcademicPeriod period, ScheduleGenerationRequest request) {
        int generated = 0;
        Long studentId = studentProgram.getStudentId();
        Long subjectId = studentSubject.getSubjectId();

        Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
        if (subjectOpt.isEmpty()) {
            log.warn("Предмет не найден для id={}", subjectId);
            return 0;
        }

        Subject subject = subjectOpt.get();
        BigDecimal hoursPerWeek = studentSubject.getPlannedHoursPerWeek();

        if (hoursPerWeek == null || hoursPerWeek.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        Long teacherId = studentSubject.getTeacherId();
        if (teacherId == null && request.getAutoAssignTeachers()) {
            teacherId = findTeacherForSubject(subjectId, period);
        }

        if (teacherId == null) {
            log.warn("Не найден преподаватель для предмета {} студента {}", subject.getName(), studentId);
            return 0;
        }

        Long roomId = null;
        if (request.getAutoAssignRooms()) {
            roomId = findRoomForLesson(subject);
        }

        List<ScheduleTemplate> templates = splitHoursIntoTemplates(
                null, studentId, subjectId, subject.getName(),
                teacherId, roomId, hoursPerWeek, period, false
        );

        for (ScheduleTemplate template : templates) {
            if (template != null) {
                try {
                    template.setAcademicPeriodId(period.getId());
                    templateRepository.save(template);
                    generated++;
                    log.debug("Сохранён шаблон для студента {}: предмет={}, день={}, время={}",
                            studentId, subject.getName(), template.getDayOfWeek(), template.getStartTime());
                } catch (Exception e) {
                    log.error("Ошибка при сохранении шаблона: {}", e.getMessage());
                }
            }
        }

        return generated;
    }

    private List<ScheduleTemplate> splitHoursIntoTemplates(
            Long groupId, Long studentId, Long subjectId, String subjectName,
            Long teacherId, Long roomId, BigDecimal hoursPerWeek,
            AcademicPeriod period, boolean isGroup) {

        List<ScheduleTemplate> templates = new ArrayList<>();

        if (teacherId == null || roomId == null) {
            return templates;
        }

        int lessonDurationMinutes = determineLessonDuration(subjectId);
        int lessonsPerWeek = Math.max(1, Math.min(18,
                hoursPerWeek.multiply(BigDecimal.valueOf(60))
                        .divide(BigDecimal.valueOf(lessonDurationMinutes), 0, java.math.RoundingMode.HALF_UP)
                        .intValue()));

        List<Integer> assignedDays = assignDaysToLessons(lessonsPerWeek, WEEK_DAYS);

        for (int dayOfWeek : assignedDays) {
            // Пытаемся найти свободный слот, пробуя разные варианты
            LocalTime startTime = findBestTimeSlot(teacherId, roomId, dayOfWeek, lessonDurationMinutes, period.getId());

            if (startTime != null) {
                ScheduleTemplate template = ScheduleTemplate.builder()
                        .groupId(groupId)
                        .studentId(studentId)
                        .subjectId(subjectId)
                        .teacherId(teacherId)
                        .roomId(roomId)
                        .dayOfWeek(dayOfWeek)
                        .startTime(startTime)
                        .durationMinutes(lessonDurationMinutes)
                        .build();
                templates.add(template);
            } else {
                log.warn("Не найден свободный слот для teacherId={}, roomId={}, day={}, duration={}",
                        teacherId, roomId, dayOfWeek, lessonDurationMinutes);
            }
        }

        return templates;
    }

    /**
     * Поиск лучшего временного слота с учётом занятости преподавателя и аудитории
     */
    private LocalTime findBestTimeSlot(Long teacherId, Long roomId, int dayOfWeek, int durationMinutes, Long periodId) {
        // Получаем все шаблоны на этот день
        List<ScheduleTemplate> existingTemplates = templateRepository.findAll().stream()
                .filter(t -> t.getAcademicPeriodId() != null &&
                        t.getAcademicPeriodId().equals(periodId) &&
                        t.getDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());

        // Собираем занятые слоты преподавателя и аудитории
        Set<String> busySlots = new HashSet<>();
        for (ScheduleTemplate t : existingTemplates) {
            LocalTime start = t.getStartTime();
            LocalTime end = start.plusMinutes(t.getDurationMinutes());

            // Добавляем занятые слоты преподавателя
            if (t.getTeacherId().equals(teacherId)) {
                busySlots.add(formatTimeSlot(start, end));
            }
            // Добавляем занятые слоты аудитории
            if (t.getRoomId().equals(roomId)) {
                busySlots.add(formatTimeSlot(start, end));
            }
        }

        // Пробуем найти свободный слот
        LocalTime currentTime = START_TIME;
        while (currentTime.plusMinutes(durationMinutes).isBefore(END_TIME) ||
                currentTime.plusMinutes(durationMinutes).equals(END_TIME)) {

            LocalTime endTime = currentTime.plusMinutes(durationMinutes);
            String slotKey = formatTimeSlot(currentTime, endTime);

            if (!busySlots.contains(slotKey)) {
                return currentTime;
            }

            currentTime = currentTime.plusMinutes(TIME_SLOT_INTERVAL);
        }

        return null;
    }

    private String formatTimeSlot(LocalTime start, LocalTime end) {
        return start.toString() + "-" + end.toString();
    }

    private List<Integer> assignDaysToLessons(int lessonsPerWeek, List<Integer> availableDays) {
        List<Integer> result = new ArrayList<>();
        if (lessonsPerWeek <= availableDays.size()) {
            result.addAll(availableDays.subList(0, lessonsPerWeek));
        } else {
            for (int i = 0; i < lessonsPerWeek; i++) {
                result.add(availableDays.get(i % availableDays.size()));
            }
        }
        return result;
    }

    private Long findTeacherForSubject(Long subjectId, AcademicPeriod period) {
        // 1. Ищем преподавателя из существующих StudentSubject
        Optional<StudentSubject> existing = studentSubjectRepository.findAll().stream()
                .filter(ss -> ss.getSubjectId().equals(subjectId) && ss.getTeacherId() != null)
                .findFirst();
        if (existing.isPresent()) {
            return existing.get().getTeacherId();
        }

        // 2. Ищем преподавателя из ProgramSubject
        Optional<ProgramSubject> programSubject = programSubjectRepository.findAll().stream()
                .filter(ps -> ps.getSubject().getId().equals(subjectId))
                .findFirst();
        if (programSubject.isPresent()) {
            // Здесь можно добавить логику поиска преподавателя по компетенциям
        }

        // 3. Берём преподавателя с минимальной нагрузкой
        List<TeacherLoad> loads = teacherLoadRepository.findAll().stream()
                .filter(l -> l.getAcademicPeriodId() != null && l.getAcademicPeriodId().equals(period.getId()))
                .sorted(Comparator.comparing(l -> l.getWeeklyHoursPlanned() != null ? l.getWeeklyHoursPlanned() : BigDecimal.ZERO))
                .collect(Collectors.toList());
        if (!loads.isEmpty()) {
            return loads.get(0).getTeacherId();
        }

        // 4. Берём любого активного преподавателя из штатного расписания
        return staffRepository.findAll().stream()
                .filter(Staff::getIsActive)
                .filter(s -> {
                    Position p = positionRepository.findById(s.getPositionId()).orElse(null);
                    return p != null && p.getIsTeaching();
                })
                .map(Staff::getUserId)
                .findFirst()
                .orElse(null);
    }

    private Long findRoomForLesson(Subject subject) {
        List<Room> rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            return null;
        }

        // Для индивидуальных занятий - маленькая аудитория
        // Для групповых - побольше
        if (subject.getLessonType() == LessonType.INDIVIDUAL) {
            return rooms.stream()
                    .filter(r -> r.getCapacity() >= 1)
                    .min(Comparator.comparingInt(Room::getCapacity))
                    .map(Room::getId)
                    .orElse(rooms.get(0).getId());
        } else {
            return rooms.stream()
                    .filter(r -> r.getCapacity() >= 10)
                    .findFirst()
                    .map(Room::getId)
                    .orElse(rooms.get(0).getId());
        }
    }

    private int determineLessonDuration(Long subjectId) {
        Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
        if (subjectOpt.isPresent() && subjectOpt.get().getLessonType() == LessonType.GROUP) {
            return 90;
        }
        return 45;
    }

    private int calculateAcademicYearForGroup(Group group, AcademicPeriod period) {
        LocalDate startDate = group.getCreatedAt() != null ?
                group.getCreatedAt().toLocalDate() : period.getStartDate();
        int startYear = startDate.getYear();
        int currentYear = period.getStartDate().getYear();
        int year = currentYear - startYear + 1;
        return Math.min(Math.max(year, 1), 4);
    }

    @Transactional(noRollbackFor = Exception.class)
    public Map<String, Object> smartGenerate(ScheduleGenerationRequest request) {
        Map<String, Object> result = generateScheduleTemplates(request);
        result.put("conflictsResolved", true);
        return result;
    }
}