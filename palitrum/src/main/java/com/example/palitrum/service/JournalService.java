package com.example.palitrum.service;

import com.example.palitrum.dto.*;
import com.example.palitrum.exception.ResourceNotFoundException;
import com.example.palitrum.model.AttendanceStatus;
import com.example.palitrum.model.*;
import com.example.palitrum.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalService {

    private final UserRepository userRepository;
    private final LessonParticipantRepository participantRepository;
    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final ProgramRepository programRepository;
    private final StudentProgramRepository studentProgramRepository;
    private final ProgramSubjectRepository programSubjectRepository;

    // ---------- уроки преподавателя ----------
    public List<TeacherLessonDto> getTeacherLessons(Long teacherId, LocalDate start, LocalDate end) {
        List<Lesson> lessons = lessonRepository.findByTeacherIdAndDateBetween(teacherId, start, end);
        return lessons.stream().map(this::toTeacherLessonDto).collect(Collectors.toList());
    }

    // ---------- участники урока ----------
    public List<LessonParticipantResponse> getParticipantsForTeacher(Long lessonId, Long teacherId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        if (!lesson.getTeacherId().equals(teacherId)) {
            throw new SecurityException("Доступ запрещён");
        }
        List<LessonParticipant> participants = participantRepository.findByLessonId(lessonId);
        return participants.stream().map(this::toParticipantResponse).collect(Collectors.toList());
    }

    // ---------- обновление участника ----------
    public LessonParticipantResponse updateParticipant(Long participantId,
                                                       LessonParticipantUpdateRequest request,
                                                       Long teacherId) {
        LessonParticipant lp = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Запись журнала не найдена"));
        Lesson lesson = lessonRepository.findById(lp.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден"));
        if (!lesson.getTeacherId().equals(teacherId)) {
            throw new SecurityException("Только преподаватель может вносить изменения");
        }
        if (request.getAttendanceStatus() != null) {
            lp.setAttendanceStatus(AttendanceStatus.valueOf(request.getAttendanceStatus().toUpperCase()));
        }
        if (request.getGradeType() != null) {
            lp.setGradeType(request.getGradeType());
        }
        if (request.getGradeValue() != null) {
            lp.setGradeValue(request.getGradeValue());
        }
        if (request.getComment() != null) {
            lp.setComment(request.getComment());
        }
        participantRepository.save(lp);
        return toParticipantResponse(lp);
    }

    // ---------- агрегированные оценки студента ----------
    public List<StudentGradeDto> getStudentGrades(Long studentId) {
        List<LessonParticipant> participants = participantRepository.findByUserId(studentId);
        Map<Long, List<LessonParticipant>> bySubject = new HashMap<>();

        for (LessonParticipant lp : participants) {
            Lesson lesson = lessonRepository.findById(lp.getLessonId()).orElse(null);
            if (lesson == null || lesson.getSubjectId() == null) continue;
            bySubject.computeIfAbsent(lesson.getSubjectId(), k -> new ArrayList<>()).add(lp);
        }

        List<StudentGradeDto> result = new ArrayList<>();
        for (Map.Entry<Long, List<LessonParticipant>> entry : bySubject.entrySet()) {
            Long subjectId = entry.getKey();
            Subject subject = subjectRepository.findById(subjectId).orElse(null);
            String subjectName = subject != null ? subject.getName() : "—";
            List<LessonParticipant> list = entry.getValue();

            double sum = 0;
            int gradeCount = 0;
            int absences = 0;
            for (LessonParticipant lp : list) {
                if (lp.getAttendanceStatus() == AttendanceStatus.ABSENT) {
                    absences++;
                }
                if (lp.getGradeValue() != null && !lp.getGradeValue().isBlank()) {
                    try {
                        double val = Double.parseDouble(lp.getGradeValue().replace(',', '.'));
                        sum += val;
                        gradeCount++;
                    } catch (NumberFormatException ignored) {}
                }
            }
            double avg = gradeCount > 0 ? Math.round(sum / gradeCount * 10.0) / 10.0 : 0;
            result.add(new StudentGradeDto(subjectId, subjectName, avg, absences));
        }
        return result;
    }


    public List<StudentProgramDto> getStudentPrograms(Long studentId) {
        List<StudentProgram> programs = studentProgramRepository.findByStudentIdAndStatus(studentId, StudentProgram.Status.ENROLLED);
        return programs.stream()
                .map(sp -> {
                    Program program = programRepository.findById(sp.getProgramId()).orElse(null);
                    return new StudentProgramDto(sp.getProgramId(), program != null ? program.getName() : "—");
                })
                .collect(Collectors.toList());
    }


    // ---------- детальные оценки по предмету ----------
    public List<GradeDetailDto> getStudentGradeDetails(Long studentId, Long subjectId, Long periodId) {
        AcademicPeriod period;
        if (periodId != null) {
            period = academicPeriodRepository.findById(periodId)
                    .orElseThrow(() -> new ResourceNotFoundException("Период не найден"));
        } else {
            period = academicPeriodRepository.findFirstByIsCurrentTrue()
                    .orElseThrow(() -> new ResourceNotFoundException("Текущий период не определён"));
        }
        LocalDate start = period.getStartDate();
        LocalDate end = period.getEndDate();

        // 1. индивидуальные уроки
        List<Lesson> individualLessons = lessonRepository.findByStudentIdAndSubjectIdAndDateBetween(studentId, subjectId, start, end);

        // 2. групповые уроки через группы студента
        List<Long> groupIds = studentGroupRepository.findActiveGroupIdsByUserId(studentId);
        List<Lesson> groupLessons = groupIds.isEmpty()
                ? Collections.emptyList()
                : lessonRepository.findByGroupIdInAndSubjectIdAndDateBetween(groupIds, subjectId, start, end);

        Set<Lesson> allLessonsSet = new LinkedHashSet<>(individualLessons);
        allLessonsSet.addAll(groupLessons);
        List<Lesson> allLessons = new ArrayList<>(allLessonsSet);

        List<GradeDetailDto> result = new ArrayList<>();
        for (Lesson lesson : allLessons) {
            LessonParticipant participant = participantRepository.findByLessonIdAndUserId(lesson.getId(), studentId).orElse(null);
            String grade = (participant != null && participant.getGradeValue() != null) ? participant.getGradeValue() : "—";
            String attendance = participant != null ? participant.getAttendanceStatus().name() : "PRESENT";

            String programName = null;
            if (lesson.getGroupId() != null) {
                Group group = groupRepository.findById(lesson.getGroupId()).orElse(null);
                if (group != null) {
                    Program program = programRepository.findById(group.getProgramId()).orElse(null);
                    programName = program != null ? program.getName() : null;
                }
            } else {
                // для индивидуальных занятий ищем любую активную программу студента, где есть этот предмет
                List<StudentProgram> activePrograms = studentProgramRepository.findByStudentIdAndStatus(studentId, StudentProgram.Status.ENROLLED);
                for (StudentProgram sp : activePrograms) {
                    if (programSubjectRepository.existsByProgramIdAndSubjectId(sp.getProgramId(), subjectId)) {
                        Program prog = programRepository.findById(sp.getProgramId()).orElse(null);
                        programName = prog != null ? prog.getName() : null;
                        break;
                    }
                }
            }

            String subjectName = subjectRepository.findById(lesson.getSubjectId())
                    .map(Subject::getName).orElse("");

            result.add(GradeDetailDto.builder()
                    .date(lesson.getDate())
                    .time(lesson.getStartTime())
                    .subjectName(subjectName)
                    .programName(programName)
                    .groupName(lesson.getGroupId() != null
                            ? groupRepository.findById(lesson.getGroupId()).map(Group::getName).orElse(null)
                            : null)
                    .studentName(lesson.getStudentId() != null
                            ? userRepository.findById(lesson.getStudentId()).map(u -> u.getFirstName() + " " + u.getLastName()).orElse(null)
                            : null)
                    .gradeValue(grade)
                    .attendanceStatus(attendance)
                    .build());
        }

        result.sort(Comparator.comparing(GradeDetailDto::getDate).thenComparing(GradeDetailDto::getTime));
        return result;
    }

    // ========== Приватные методы маппинга ==========
    private TeacherLessonDto toTeacherLessonDto(Lesson lesson) {
        String subjectName = subjectRepository.findById(lesson.getSubjectId())
                .map(Subject::getName)
                .orElse(null);
        String groupName = null;
        String studentName = null;
        if (lesson.getGroupId() != null) {
            groupName = groupRepository.findById(lesson.getGroupId())
                    .map(Group::getName)
                    .orElse(null);
        } else if (lesson.getStudentId() != null) {
            User student = userRepository.findById(lesson.getStudentId()).orElse(null);
            if (student != null) {
                studentName = buildFullName(student.getFirstName(), student.getLastName());
            }
        }
        return TeacherLessonDto.builder()
                .id(lesson.getId())
                .date(lesson.getDate())
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .subjectName(subjectName)
                .groupName(groupName)
                .studentName(studentName)
                .build();
    }

    private LessonParticipantResponse toParticipantResponse(LessonParticipant lp) {
        User student = userRepository.findById(lp.getUserId()).orElse(null);
        String fullName = "Неизвестный студент";
        if (student != null) {
            fullName = buildFullName(student.getFirstName(), student.getLastName());
            if (fullName.isEmpty()) {
                fullName = "Студент ID " + student.getId();
            }
        }

        LessonParticipantResponse resp = new LessonParticipantResponse();
        resp.setId(lp.getId());
        resp.setStudentId(lp.getUserId());
        resp.setStudentFullName(fullName);
        resp.setAttendanceStatus(lp.getAttendanceStatus().name());
        resp.setGradeType(lp.getGradeType());
        resp.setGradeValue(lp.getGradeValue());
        resp.setComment(lp.getComment());
        resp.setUpdatedAt(lp.getUpdatedAt());
        return resp;
    }

    private String buildFullName(String firstName, String lastName) {
        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";
        return String.join(" ", first, last).trim();
    }
}