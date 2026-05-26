package com.example.palitrum.service;

import com.example.palitrum.model.*;
import com.example.palitrum.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final ProgramRepository programRepository;
    private final ProgramSubjectRepository programSubjectRepository;
    private final GroupRepository groupRepository;
    private final StudentProgramRepository studentProgramRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentGroupRepository studentGroupRepository;

    @Transactional
    public void enrollFromApplication(Application application, int academicYear) {
        Long studentId = application.getChildUserId();
        if (studentId == null) {
            throw new IllegalStateException("У заявки нет созданного пользователя-ребёнка");
        }

        Long programId = application.getFinalProgramId() != null
                ? application.getFinalProgramId()
                : application.getPreferredProgramId();
        if (programId == null) {
            throw new IllegalStateException("В заявке не указана программа для зачисления");
        }

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Программа не найдена: " + programId));

        // Создаём student_program
        StudentProgram studentProgram = StudentProgram.builder()
                .studentId(studentId)
                .programId(program.getId())
                .enrollmentDate(LocalDate.now())
                .status(StudentProgram.Status.ENROLLED)
                .build();
        studentProgramRepository.save(studentProgram);
        log.info("Создана student_program id={} для студента {} по программе {}", studentProgram.getId(), studentId, program.getName());

        // Получаем предметы программы для указанного года
        List<ProgramSubject> programSubjects = programSubjectRepository.findByProgramIdAndAcademicYear(program.getId(), academicYear);
        if (programSubjects.isEmpty()) {
            log.warn("Для программы {} и года {} нет предметов. Учебный план не создан.", program.getName(), academicYear);
            return;
        }

        for (ProgramSubject ps : programSubjects) {
            Subject subject = ps.getSubject();
            boolean isGroupLesson = subject.getLessonType() == LessonType.GROUP;

            // Создаём StudentSubject через конструктор или сеттеры
            StudentSubject studentSubject = new StudentSubject();
            studentSubject.setStudentProgramId(studentProgram.getId());
            studentSubject.setSubjectId(subject.getId());
            studentSubject.setPlannedHoursPerWeek(ps.getHoursPerWeekForProgram());
            studentSubject.setIsGroupLesson(isGroupLesson);  // обратите внимание на имя метода setIsGroupLesson
            if (isGroupLesson) {
                Group group = groupRepository.findByProgramIdAndAcademicYearAndSubjectId(program.getId(), academicYear, subject.getId())
                        .orElseGet(() -> {
                            Group newGroup = Group.builder()
                                    .programId(program.getId())
                                    .name(generateGroupName(program, academicYear, subject))
                                    .academicYear(academicYear)
                                    .subjectId(subject.getId())
                                    .status("active")
                                    .build();
                            log.info("Создана новая группа: {}", newGroup.getName());
                            return groupRepository.save(newGroup);
                        });
                studentSubject.setGroupId(group.getId());

                // Добавляем студента в группу
                StudentGroup studentGroup = StudentGroup.builder()
                        .groupId(group.getId())
                        .userId(studentId)
                        .enrolledDate(LocalDate.now())
                        .enrollmentStatus(StudentGroup.EnrollmentStatus.ENROLLED)
                        .active(true)
                        .sourceApplicationId(application.getId())
                        .build();
                studentGroupRepository.save(studentGroup);
                log.info("Студент {} добавлен в группу {}", studentId, group.getId());
            } else {
                studentSubject.setGroupId(null);
            }

            studentSubjectRepository.save(studentSubject);
            log.info("Создан student_subject для предмета {} (групповой: {})", subject.getName(), isGroupLesson);
        }
    }

    private String generateGroupName(Program program, int academicYear, Subject subject) {
        return program.getName() + "-" + academicYear + "-" + subject.getName().replaceAll("\\s+", "");
    }
}