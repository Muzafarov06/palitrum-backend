package com.example.palitrum.service;

import com.example.palitrum.dto.CalendarEventDto;
import com.example.palitrum.dto.LessonDTO;
import com.example.palitrum.model.*;
import com.example.palitrum.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final TeacherLoadRepository teacherLoadRepository; // новая зависимость

    public LessonService(LessonRepository lessonRepository,
                         SubjectRepository subjectRepository,
                         GroupRepository groupRepository,
                         UserRepository userRepository,
                         RoomRepository roomRepository,
                         AcademicPeriodRepository academicPeriodRepository,
                         StudentGroupRepository studentGroupRepository,
                         TeacherLoadRepository teacherLoadRepository) {
        this.lessonRepository = lessonRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.academicPeriodRepository = academicPeriodRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.teacherLoadRepository = teacherLoadRepository;
    }

    private LessonDTO toDto(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setGroupId(lesson.getGroupId());
        dto.setStudentId(lesson.getStudentId());
        dto.setSubjectId(lesson.getSubjectId());
        dto.setDate(lesson.getDate());
        dto.setStartTime(lesson.getStartTime());
        dto.setEndTime(lesson.getEndTime());
        dto.setDurationMinutes(lesson.getDurationMinutes());
        dto.setRoomId(lesson.getRoomId());
        dto.setTeacherId(lesson.getTeacherId());
        dto.setSubstituteTeacherId(lesson.getSubstituteTeacherId());
        dto.setLessonType(lesson.getLessonType());
        dto.setStatus(lesson.getStatus());
        dto.setNotes(lesson.getNotes());
        dto.setCreatedBy(lesson.getCreatedBy());
        dto.setCreatedAt(lesson.getCreatedAt());
        return dto;
    }

    public List<CalendarEventDto> getEventsForPeriod(LocalDate startDate, LocalDate endDate,
                                                     Long groupId, Long studentId, Long teacherId,
                                                     Long programId, Long periodId) {
        // если задан период и не заданы даты – берём границы периода
        if (periodId != null && (startDate == null || endDate == null)) {
            AcademicPeriod period = academicPeriodRepository.findById(periodId)
                    .orElseThrow(() -> new IllegalArgumentException("Учебный период не найден"));
            if (startDate == null) startDate = period.getStartDate();
            if (endDate == null) endDate = period.getEndDate();
        } else if (periodId != null) {
            // пересечение дат запроса и периода
            AcademicPeriod period = academicPeriodRepository.findById(periodId)
                    .orElseThrow(() -> new IllegalArgumentException("Учебный период не найден"));
            if (period.getStartDate().isAfter(startDate)) startDate = period.getStartDate();
            if (period.getEndDate().isBefore(endDate)) endDate = period.getEndDate();
        }

        List<Lesson> lessons;

        if (programId != null) {
            List<Long> groupIds = groupRepository.findGroupIdsByProgramId(programId);
            if (groupIds.isEmpty()) {
                lessons = Collections.emptyList();
            } else {
                lessons = lessonRepository.findByGroupIdInAndDateBetween(groupIds, startDate, endDate);
            }
        } else if (studentId != null) {
            List<Lesson> allLessons = new ArrayList<>();
            allLessons.addAll(lessonRepository.findByStudentIdAndDateBetween(studentId, startDate, endDate));

            List<Long> groupIds = studentGroupRepository.findActiveGroupIdsByUserId(studentId);
            if (!groupIds.isEmpty()) {
                allLessons.addAll(lessonRepository.findByGroupIdInAndDateBetween(groupIds, startDate, endDate));
            }
            lessons = allLessons;
        } else if (groupId != null) {
            lessons = lessonRepository.findByGroupIdAndDateBetween(groupId, startDate, endDate);
        } else if (teacherId != null) {
            lessons = lessonRepository.findByTeacherIdAndDateBetween(teacherId, startDate, endDate);
        } else {
            lessons = lessonRepository.findByDateBetween(startDate, endDate);
        }

        return lessons.stream().map(this::toCalendarDto).collect(Collectors.toList());
    }

    private CalendarEventDto toCalendarDto(Lesson lesson) {
        CalendarEventDto dto = new CalendarEventDto();
        dto.setId(lesson.getId());
        String subjectName = subjectRepository.findById(lesson.getSubjectId())
                .map(Subject::getName).orElse("?");
        String participant = "";
        if (lesson.getGroupId() != null) {
            participant = groupRepository.findById(lesson.getGroupId()).map(Group::getName).orElse("");
            dto.setGroupId(lesson.getGroupId());
            dto.setGroupName(participant);
        } else if (lesson.getStudentId() != null) {
            participant = userRepository.findById(lesson.getStudentId())
                    .map(u -> u.getFirstName() + " " + u.getLastName()).orElse("");
            dto.setStudentId(lesson.getStudentId());
            dto.setStudentName(participant);
        }
        dto.setTitle(subjectName + (participant.isEmpty() ? "" : " (" + participant + ")"));
        dto.setDate(lesson.getDate());
        dto.setStartTime(lesson.getStartTime());
        dto.setEndTime(lesson.getEndTime());
        dto.setRoomId(lesson.getRoomId());
        roomRepository.findById(lesson.getRoomId()).ifPresent(r -> dto.setRoomName(r.getName()));
        dto.setTeacherId(lesson.getTeacherId());
        userRepository.findById(lesson.getTeacherId()).ifPresent(t -> dto.setTeacherName(t.getFirstName() + " " + t.getLastName()));
        dto.setStatus(lesson.getStatus().name());
        return dto;
    }

    public List<LessonDTO> getAll() {
        return lessonRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public LessonDTO getById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Занятие не найдено"));
        return toDto(lesson);
    }

    /**
     * Проверка конфликтов времени: преподаватель, комната, студент (если есть), группа (если есть).
     */
    public void validateConflict(Lesson lesson, Long excludeId) {
        if (lesson.getEndTime().isBefore(lesson.getStartTime()) || lesson.getEndTime().equals(lesson.getStartTime())) {
            throw new IllegalArgumentException("Время окончания должно быть позже времени начала");
        }
        if (lesson.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Длительность должна быть положительной");
        }
        if (lessonRepository.existsConflictForTeacher(lesson.getTeacherId(), lesson.getDate(),
                lesson.getStartTime(), lesson.getEndTime(), excludeId)) {
            throw new IllegalArgumentException("Преподаватель уже занят в это время");
        }
        if (lessonRepository.existsConflictForRoom(lesson.getRoomId(), lesson.getDate(),
                lesson.getStartTime(), lesson.getEndTime(), excludeId)) {
            throw new IllegalArgumentException("Аудитория уже занята в это время");
        }
        if (lesson.getStudentId() != null && lessonRepository.existsConflictForStudent(lesson.getStudentId(), lesson.getDate(),
                lesson.getStartTime(), lesson.getEndTime(), excludeId)) {
            throw new IllegalArgumentException("Студент уже занят в это время");
        }
        if (lesson.getGroupId() != null && lessonRepository.existsConflictForGroup(lesson.getGroupId(), lesson.getDate(),
                lesson.getStartTime(), lesson.getEndTime(), excludeId)) {
            throw new IllegalArgumentException("Группа уже занята в это время");
        }
    }

    /**
     * Проверка недельной нагрузки преподавателя (sum duration <= max_weekly_hours).
     */
    public void checkTeacherLoad(Long teacherId, LocalDate lessonDate, int durationMinutes, Long excludeLessonId) {
        AcademicPeriod period = academicPeriodRepository.findFirstByIsCurrentTrue().orElse(null);
        if (period == null) return;

        LocalDate weekStart = lessonDate.with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        List<Lesson> existingLessons = lessonRepository.findByTeacherIdAndDateBetween(teacherId, weekStart, weekEnd);

        int totalMinutes = 0;
        for (Lesson l : existingLessons) {
            if (excludeLessonId != null && l.getId().equals(excludeLessonId)) continue;
            if (l.getStatus() == Lesson.LessonStatus.CANCELLED) continue;
            totalMinutes += l.getDurationMinutes();
        }
        totalMinutes += durationMinutes;

        TeacherLoad load = teacherLoadRepository.findByTeacherIdAndAcademicPeriodId(teacherId, period.getId()).orElse(null);
        if (load != null && load.getMaxWeeklyHours() != null) {
            int maxMinutes = load.getMaxWeeklyHours().multiply(BigDecimal.valueOf(60)).intValue();
            if (totalMinutes > maxMinutes) {
                throw new IllegalArgumentException(
                        String.format("Преподаватель будет перегружен: %d мин в неделю (макс. %d мин)", totalMinutes, maxMinutes));
            }
        }
    }

    @Transactional
    public LessonDTO create(LessonDTO dto) {
        Lesson lesson = new Lesson();
        lesson.setGroupId(dto.getGroupId());
        lesson.setStudentId(dto.getStudentId());
        lesson.setSubjectId(dto.getSubjectId());
        lesson.setDate(dto.getDate());
        lesson.setStartTime(dto.getStartTime());
        lesson.setEndTime(dto.getEndTime());
        lesson.setDurationMinutes(dto.getDurationMinutes());
        lesson.setRoomId(dto.getRoomId());
        lesson.setTeacherId(dto.getTeacherId());
        lesson.setSubstituteTeacherId(dto.getSubstituteTeacherId());
        lesson.setLessonType(dto.getLessonType());
        lesson.setStatus(dto.getStatus() != null ? dto.getStatus() : Lesson.LessonStatus.PLANNED);
        lesson.setNotes(dto.getNotes());
        lesson.setCreatedBy(dto.getCreatedBy());
        lesson.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : OffsetDateTime.now());

        validateConflict(lesson, null);
        checkTeacherLoad(lesson.getTeacherId(), lesson.getDate(), lesson.getDurationMinutes(), null);
        lessonRepository.save(lesson);
        dto.setId(lesson.getId());
        return dto;
    }

    @Transactional
    public LessonDTO update(Long id, LessonDTO dto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Занятие не найдено"));

        lesson.setGroupId(dto.getGroupId());
        lesson.setStudentId(dto.getStudentId());
        lesson.setSubjectId(dto.getSubjectId());
        lesson.setDate(dto.getDate());
        lesson.setStartTime(dto.getStartTime());
        lesson.setEndTime(dto.getEndTime());
        lesson.setDurationMinutes(dto.getDurationMinutes());
        lesson.setRoomId(dto.getRoomId());
        lesson.setTeacherId(dto.getTeacherId());
        lesson.setSubstituteTeacherId(dto.getSubstituteTeacherId());
        lesson.setLessonType(dto.getLessonType());
        lesson.setStatus(dto.getStatus());
        lesson.setNotes(dto.getNotes());

        validateConflict(lesson, id);
        checkTeacherLoad(lesson.getTeacherId(), lesson.getDate(), lesson.getDurationMinutes(), id);
        lessonRepository.save(lesson);
        return toDto(lesson);
    }

    @Transactional
    public void delete(Long id) {
        lessonRepository.deleteById(id);
    }
}