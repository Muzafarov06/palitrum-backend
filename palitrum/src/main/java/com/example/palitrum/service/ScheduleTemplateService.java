package com.example.palitrum.service;

import com.example.palitrum.dto.ScheduleTemplateDto;
import com.example.palitrum.dto.ScheduleTemplateResponse;
import com.example.palitrum.model.*;
import com.example.palitrum.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleTemplateService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleTemplateService.class);

    private final ScheduleTemplateRepository repository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final AcademicPeriodRepository periodRepository;
    private final LessonRepository lessonRepository;
    private final LessonParticipantRepository lessonParticipantRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final UserService userService;
    private final LessonService lessonService;

    // ========== CRUD шаблонов ==========
    @Transactional(readOnly = true)
    public Page<ScheduleTemplateResponse> getAllByPeriod(Long periodId, Pageable pageable) {
        return repository.findByAcademicPeriodId(periodId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ScheduleTemplateResponse create(ScheduleTemplateDto dto) {
        validateDto(dto);
        validateTemplateFeasibility(dto);
        ScheduleTemplate template = buildEntity(dto);
        return toResponse(repository.save(template));
    }

    @Transactional
    public ScheduleTemplateResponse update(Long id, ScheduleTemplateDto dto) {
        ScheduleTemplate template = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Шаблон не найден"));
        validateDto(dto);
        validateTemplateFeasibility(dto);
        updateEntity(template, dto);
        return toResponse(repository.save(template));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ========== Генерация уроков с возвратом ошибок ==========
    @Transactional
    public Map<String, Object> generateLessonsWithErrors(Long periodId) {
        AcademicPeriod period = periodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("Период не найден"));

        List<ScheduleTemplate> templates = repository.findByAcademicPeriodId(periodId, Pageable.unpaged()).getContent();
        if (templates.isEmpty()) return Map.of("generated", 0);

        Long currentUserId = userService.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Не удалось определить текущего пользователя");
        }

        LocalDate start = period.getStartDate();
        LocalDate end = period.getEndDate();
        List<String> errors = new ArrayList<>();
        List<Lesson> lessonsToSave = new ArrayList<>();
        Map<Lesson, List<LessonParticipant>> participantsMap = new LinkedHashMap<>();

        for (ScheduleTemplate tpl : templates) {
            List<LocalDate> dates = getDatesForDayOfWeek(start, end, tpl.getDayOfWeek());
            for (LocalDate date : dates) {
                if (lessonAlreadyExists(tpl, date)) continue;

                Lesson lesson = new Lesson();
                lesson.setGroupId(tpl.getGroupId());
                lesson.setStudentId(tpl.getStudentId());
                lesson.setSubjectId(tpl.getSubjectId());
                lesson.setDate(date);
                lesson.setStartTime(tpl.getStartTime());
                lesson.setEndTime(tpl.getStartTime().plusMinutes(tpl.getDurationMinutes()));
                lesson.setDurationMinutes(tpl.getDurationMinutes());
                lesson.setRoomId(tpl.getRoomId());
                lesson.setTeacherId(tpl.getTeacherId());
                lesson.setLessonType(Lesson.LessonType.CLASS);
                lesson.setStatus(Lesson.LessonStatus.PLANNED);
                lesson.setNotes(null);
                lesson.setCreatedBy(currentUserId);

                // Проверяем конфликты
                try {
                    lessonService.validateConflict(lesson, null);
                    lessonService.checkTeacherLoad(lesson.getTeacherId(), lesson.getDate(),
                            lesson.getDurationMinutes(), null);
                } catch (IllegalArgumentException ex) {
                    String who = tpl.getGroupId() != null
                            ? "Группа " + getGroupName(tpl.getGroupId())
                            : "Студент " + getUserName(tpl.getStudentId());
                    errors.add(String.format("%s — %s: %s", date, who, ex.getMessage()));
                    continue;
                }

                lessonsToSave.add(lesson);
                List<LessonParticipant> participants = new ArrayList<>();

                if (tpl.getGroupId() != null) {
                    List<StudentGroup> members = studentGroupRepository.findByGroupIdAndEnrollmentStatus(
                            tpl.getGroupId(), StudentGroup.EnrollmentStatus.ENROLLED);
                    for (StudentGroup sg : members) {
                        LessonParticipant participant = new LessonParticipant();
                        participant.setUserId(sg.getUserId());
                        participant.setAttendanceStatus(AttendanceStatus.PRESENT);
                        participant.setGradeType("NONE");
                        participant.setRecordedBy(currentUserId);
                        participants.add(participant);
                    }
                } else if (tpl.getStudentId() != null) {
                    LessonParticipant participant = new LessonParticipant();
                    participant.setUserId(tpl.getStudentId());
                    participant.setAttendanceStatus(AttendanceStatus.PRESENT);
                    participant.setGradeType("NONE");
                    participant.setRecordedBy(currentUserId);
                    participants.add(participant);
                }
                participantsMap.put(lesson, participants);
            }
        }

        if (!errors.isEmpty()) {
            Map<String, Object> errorResult = new LinkedHashMap<>();
            errorResult.put("generated", lessonsToSave.size());
            errorResult.put("errors", errors);
            return errorResult;
        }

        List<Lesson> savedLessons = lessonRepository.saveAll(lessonsToSave);
        int idx = 0;
        for (Lesson savedLesson : savedLessons) {
            List<LessonParticipant> parts = participantsMap.get(lessonsToSave.get(idx));
            if (parts != null) {
                for (LessonParticipant p : parts) {
                    p.setLessonId(savedLesson.getId());
                }
                lessonParticipantRepository.saveAll(parts);
            }
            idx++;
        }

        return Map.of("generated", savedLessons.size());
    }

    @Transactional
    public int generateLessons(Long periodId) {
        Map<String, Object> result = generateLessonsWithErrors(periodId);
        if (result.containsKey("errors") && !((List<?>) result.get("errors")).isEmpty()) {
            throw new RuntimeException("Ошибки генерации: " + result.get("errors"));
        }
        return (int) result.get("generated");
    }

    // ========== Проверка возможности генерации ==========
    private void validateTemplateFeasibility(ScheduleTemplateDto dto) {
        AcademicPeriod period = periodRepository.findById(dto.getAcademicPeriodId())
                .orElseThrow(() -> new RuntimeException("Период не найден"));

        List<LocalDate> dates = getDatesForDayOfWeek(
                period.getStartDate(), period.getEndDate(), dto.getDayOfWeek());
        if (dates.isEmpty()) return;

        LocalDate firstDate = dates.get(0);

        Lesson testLesson = new Lesson();
        testLesson.setGroupId(dto.getGroupId());
        testLesson.setStudentId(dto.getStudentId());
        testLesson.setSubjectId(dto.getSubjectId());
        testLesson.setDate(firstDate);
        testLesson.setStartTime(dto.getStartTime());
        testLesson.setEndTime(dto.getStartTime().plusMinutes(dto.getDurationMinutes()));
        testLesson.setDurationMinutes(dto.getDurationMinutes());
        testLesson.setRoomId(dto.getRoomId());
        testLesson.setTeacherId(dto.getTeacherId());

        try {
            lessonService.validateConflict(testLesson, null);
            lessonService.checkTeacherLoad(testLesson.getTeacherId(), testLesson.getDate(),
                    testLesson.getDurationMinutes(), null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Невозможно создать шаблон: " + e.getMessage());
        }
    }

    // ========== Вспомогательные методы ==========
    private String getGroupName(Long groupId) {
        return groupRepository.findById(groupId).map(Group::getName).orElse(String.valueOf(groupId));
    }

    private String getUserName(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse(String.valueOf(userId));
    }

    private List<LocalDate> getDatesForDayOfWeek(LocalDate start, LocalDate end, int dayOfWeek) {
        List<LocalDate> dates = new ArrayList<>();
        DayOfWeek target = DayOfWeek.of(dayOfWeek);
        LocalDate current = start.with(target);
        if (current.isBefore(start)) current = current.plusWeeks(1);
        while (!current.isAfter(end)) {
            dates.add(current);
            current = current.plusWeeks(1);
        }
        return dates;
    }

    private boolean lessonAlreadyExists(ScheduleTemplate tpl, LocalDate date) {
        if (tpl.getGroupId() != null) {
            return lessonRepository.findByDateAndGroupId(date, tpl.getGroupId()).stream()
                    .anyMatch(l -> l.getStartTime().equals(tpl.getStartTime()) &&
                            l.getRoomId().equals(tpl.getRoomId()));
        } else if (tpl.getStudentId() != null) {
            return lessonRepository.findByDateAndStudentId(date, tpl.getStudentId()).stream()
                    .anyMatch(l -> l.getStartTime().equals(tpl.getStartTime()) &&
                            l.getRoomId().equals(tpl.getRoomId()));
        }
        return false;
    }

    private void validateDto(ScheduleTemplateDto dto) {
        if (dto.getGroupId() == null && dto.getStudentId() == null) {
            throw new IllegalArgumentException("Должна быть указана либо группа, либо студент");
        }
        if (dto.getGroupId() != null && dto.getStudentId() != null) {
            throw new IllegalArgumentException("Нельзя указывать одновременно группу и студента");
        }
        if (!subjectRepository.existsById(dto.getSubjectId()))
            throw new RuntimeException("Предмет не найден");
        if (!userRepository.existsById(dto.getTeacherId()))
            throw new RuntimeException("Преподаватель не найден");
        if (!roomRepository.existsById(dto.getRoomId()))
            throw new RuntimeException("Помещение не найдено");
        if (!periodRepository.existsById(dto.getAcademicPeriodId()))
            throw new RuntimeException("Учебный период не найден");
        if (dto.getGroupId() != null && !groupRepository.existsById(dto.getGroupId()))
            throw new RuntimeException("Группа не найдена");
        if (dto.getStudentId() != null && !userRepository.existsById(dto.getStudentId()))
            throw new RuntimeException("Студент не найден");
    }

    private ScheduleTemplate buildEntity(ScheduleTemplateDto dto) {
        return ScheduleTemplate.builder()
                .subjectId(dto.getSubjectId())
                .groupId(dto.getGroupId())
                .studentId(dto.getStudentId())
                .teacherId(dto.getTeacherId())
                .roomId(dto.getRoomId())
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .durationMinutes(dto.getDurationMinutes())
                .academicPeriodId(dto.getAcademicPeriodId())
                .build();
    }

    private void updateEntity(ScheduleTemplate template, ScheduleTemplateDto dto) {
        template.setSubjectId(dto.getSubjectId());
        template.setGroupId(dto.getGroupId());
        template.setStudentId(dto.getStudentId());
        template.setTeacherId(dto.getTeacherId());
        template.setRoomId(dto.getRoomId());
        template.setDayOfWeek(dto.getDayOfWeek());
        template.setStartTime(dto.getStartTime());
        template.setDurationMinutes(dto.getDurationMinutes());
        template.setAcademicPeriodId(dto.getAcademicPeriodId());
    }

    private ScheduleTemplateResponse toResponse(ScheduleTemplate template) {
        Subject subject = subjectRepository.findById(template.getSubjectId()).orElse(null);
        Group group = template.getGroupId() != null ? groupRepository.findById(template.getGroupId()).orElse(null) : null;
        User student = template.getStudentId() != null ? userRepository.findById(template.getStudentId()).orElse(null) : null;
        User teacher = userRepository.findById(template.getTeacherId()).orElse(null);
        Room room = roomRepository.findById(template.getRoomId()).orElse(null);
        AcademicPeriod period = periodRepository.findById(template.getAcademicPeriodId()).orElse(null);

        return new ScheduleTemplateResponse(
                template.getId(),
                template.getSubjectId(),
                subject != null ? subject.getName() : null,
                template.getGroupId(),
                group != null ? group.getName() : null,
                template.getStudentId(),
                student != null ? student.getFirstName() + " " + student.getLastName() : null,
                template.getTeacherId(),
                teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : null,
                template.getRoomId(),
                room != null ? room.getName() : null,
                template.getDayOfWeek(),
                template.getStartTime(),
                template.getDurationMinutes(),
                template.getAcademicPeriodId(),
                period != null ? period.getName() : null,
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}