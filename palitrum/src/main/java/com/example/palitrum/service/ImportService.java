package com.example.palitrum.service;

import com.example.palitrum.dto.importDTO.*;
import com.example.palitrum.model.*;
import com.example.palitrum.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final RoomRepository roomRepository;
    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;
    private final ProgramSubjectRepository programSubjectRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final DepartmentRepository departmentRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final ProgramDepartmentRepository programDepartmentRepository; // добавлено для связей отделение-программа
    private final PositionRepository positionRepository;
    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRelationRepository userRelationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void importPrograms(List<ProgramImportDTO> dtos) {
        for (ProgramImportDTO dto : dtos) {
            if (programRepository.existsByName(dto.name())) continue;
            Program program = Program.builder()
                    .name(dto.name())
                    .description(dto.description())
                    .durationYears(dto.durationYears() != null ? dto.durationYears() : 4)
                    .build();
            programRepository.save(program);
        }
    }

    @Transactional
    public void importDepartments(List<DepartmentImportDTO> dtos) {
        Map<String, Department> existingByName = departmentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Department::getName, Function.identity(), (a, b) -> a));

        for (DepartmentImportDTO dto : dtos) {
            if (existingByName.containsKey(dto.name())) {
                continue; // уже есть, пропускаем
            }
            Department parent = null;
            if (dto.parentName() != null && !dto.parentName().isBlank()) {
                parent = existingByName.get(dto.parentName());
                if (parent == null) {
                    System.err.println("Родительское отделение '" + dto.parentName() + "' не найдено, отделение '" + dto.name() + "' будет корневым.");
                }
            }
            Department dept = Department.builder()
                    .name(dto.name())
                    .description(dto.description())
                    .parent(parent)
                    .build();
            departmentRepository.save(dept);
            existingByName.put(dto.name(), dept);
        }
    }

    /**
     * Импорт отделений, программ и связей между ними (единый импорт)
     */
    /**
     * Импорт отделений, программ и связей между ними (единый импорт)
     */
    @Transactional
    public void importDepartmentsPrograms(List<DepartmentImportDTO> deptDtos,
                                          List<ProgramImportDTO> progDtos,
                                          List<DepartmentProgramLinkDTO> linkDtos) {
        System.out.println("=== ImportService.importDepartmentsPrograms ===");
        System.out.println("Получено отделений: " + deptDtos.size());
        System.out.println("Получено программ: " + progDtos.size());
        System.out.println("Получено связей: " + linkDtos.size());

        // 1. Импорт отделений
        Map<String, Department> deptMap = new HashMap<>();
        System.out.println("=== Импорт отделений ===");

        for (DepartmentImportDTO dto : deptDtos) {
            Optional<Department> existing = departmentRepository.findByName(dto.name());
            if (existing.isPresent()) {
                System.out.println("  Отделение уже существует: " + dto.name());
                deptMap.put(dto.name(), existing.get());
                continue;
            }

            Department parent = null;
            if (dto.parentName() != null && !dto.parentName().isBlank()) {
                parent = deptMap.get(dto.parentName());
                if (parent == null) {
                    parent = departmentRepository.findByName(dto.parentName()).orElse(null);
                }
            }

            Department dept = Department.builder()
                    .name(dto.name())
                    .description(dto.description())
                    .parent(parent)
                    .build();
            Department saved = departmentRepository.save(dept);
            deptMap.put(dto.name(), saved);
            System.out.println("  Создано отделение: " + saved.getName() + " (ID: " + saved.getId() + ")");
        }
        departmentRepository.flush();
        System.out.println("=== Отделения сохранены в БД ===");

        // 2. Импорт программ
        Map<String, Program> progMap = new HashMap<>();
        System.out.println("=== Импорт программ ===");

        for (ProgramImportDTO dto : progDtos) {
            Optional<Program> existing = programRepository.findByName(dto.name());
            if (existing.isPresent()) {
                System.out.println("  Программа уже существует: " + dto.name());
                progMap.put(dto.name(), existing.get());
                continue;
            }

            Program prog = Program.builder()
                    .name(dto.name())
                    .description(dto.description())
                    .durationYears(dto.durationYears() != null ? dto.durationYears() : 4)
                    .build();
            Program saved = programRepository.save(prog);
            progMap.put(dto.name(), saved);
            System.out.println("  Создана программа: " + saved.getName() + " (ID: " + saved.getId() + ")");
        }
        programRepository.flush();
        System.out.println("=== Программы сохранены в БД ===");

        // 3. Импорт связей (В САМОМ КОНЦЕ, после того как все отделения и программы точно в БД)
        System.out.println("=== Импорт связей ===");
        System.out.println("Начинаем импорт " + linkDtos.size() + " связей...");
        int savedLinks = 0;
        int failedLinks = 0;

        for (DepartmentProgramLinkDTO link : linkDtos) {
            System.out.println("  Обработка связи: " + link.departmentName() + " -> " + link.programName());

            // Ищем отделение (сначала в мапе, потом в БД)
            Department dept = deptMap.get(link.departmentName());
            if (dept == null) {
                dept = departmentRepository.findByName(link.departmentName()).orElse(null);
                if (dept != null) {
                    deptMap.put(link.departmentName(), dept);
                }
            }

            if (dept == null) {
                System.err.println("    ОШИБКА: Отделение не найдено: " + link.departmentName());
                failedLinks++;
                continue;
            }

            // Ищем программу (сначала в мапе, потом в БД)
            Program prog = progMap.get(link.programName());
            if (prog == null) {
                prog = programRepository.findByName(link.programName()).orElse(null);
                if (prog != null) {
                    progMap.put(link.programName(), prog);
                }
            }

            if (prog == null) {
                System.err.println("    ОШИБКА: Программа не найдена: " + link.programName());
                failedLinks++;
                continue;
            }

            System.out.println("    Найдено: отделение ID=" + dept.getId() + ", программа ID=" + prog.getId());

            // Проверяем существование связи
            boolean exists = programDepartmentRepository.existsByProgramIdAndDepartmentId(prog.getId(), dept.getId());
            System.out.println("    Существует ли связь? " + exists);

            if (!exists) {
                try {
                    ProgramDepartment pd = ProgramDepartment.builder()
                            .program(prog)
                            .department(dept)
                            .isPrimary(link.isPrimary() != null ? link.isPrimary() : false)
                            .notes(link.notes() != null ? link.notes() : "")
                            .build();

                    ProgramDepartment saved = programDepartmentRepository.save(pd);
                    // Принудительно сбрасываем в БД
                    programDepartmentRepository.flush();
                    savedLinks++;
                    System.out.println("    ✓ СОХРАНЕНА связь ID: " + saved.getId());
                } catch (Exception e) {
                    System.err.println("    ✗ ОШИБКА при сохранении связи: " + e.getMessage());
                    e.printStackTrace();
                    failedLinks++;
                }
            } else {
                System.out.println("    Связь уже существует, пропускаем");
            }
        }

        System.out.println("=== РЕЗУЛЬТАТ ИМПОРТА СВЯЗЕЙ ===");
        System.out.println("  Успешно сохранено: " + savedLinks);
        System.out.println("  Пропущено/ошибок: " + failedLinks);
        System.out.println("  Всего обработано: " + linkDtos.size());
    }

    @Transactional
    public void importSubjects(List<SubjectImportDTO> dtos) {
        for (SubjectImportDTO dto : dtos) {
            if (subjectRepository.existsByCode(dto.code())) continue;
            Program defaultProgram = null;
            if (dto.defaultProgramName() != null && !dto.defaultProgramName().isBlank()) {
                defaultProgram = programRepository.findByName(dto.defaultProgramName()).orElse(null);
            }
            Subject subject = Subject.builder()
                    .code(dto.code())
                    .name(dto.name())
                    .description(dto.description())
                    .standardHoursPerWeek(dto.standardHoursPerWeek())
                    .lessonType(LessonType.valueOf(dto.lessonType()))
                    .minGroupSize(dto.minGroupSize())
                    .maxGroupSize(dto.maxGroupSize())
                    .defaultProgram(defaultProgram)
                    .build();
            subjectRepository.save(subject);
        }
    }

    @Transactional
    public void importAcademicPeriods(List<AcademicPeriodImportDTO> dtos) {
        // Проверяем, есть ли в импорте периоды с isCurrent = true
        boolean hasCurrent = dtos.stream().anyMatch(AcademicPeriodImportDTO::isCurrent);

        if (hasCurrent) {
            // Сбрасываем флаг isCurrent у всех существующих периодов
            academicPeriodRepository.resetCurrentFlag();
        }

        for (AcademicPeriodImportDTO dto : dtos) {
            // Проверяем, существует ли период с таким именем
            Optional<AcademicPeriod> existing = academicPeriodRepository.findByName(dto.name());
            if (existing.isPresent()) {
                // Обновляем существующий период
                AcademicPeriod period = existing.get();
                period.setStartDate(dto.startDate());
                period.setEndDate(dto.endDate());
                period.setPeriodType(AcademicPeriod.PeriodType.valueOf(dto.periodType().toUpperCase()));
                // Если в импорте isCurrent = true, а это единственный текущий период - устанавливаем
                if (dto.isCurrent()) {
                    period.setIsCurrent(true);
                } else {
                    period.setIsCurrent(false);
                }
                academicPeriodRepository.save(period);
                continue;
            }

            AcademicPeriod.PeriodType type;
            try {
                type = AcademicPeriod.PeriodType.valueOf(dto.periodType().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Некорректный тип периода: " + dto.periodType() + " для периода " + dto.name());
                continue;
            }

            AcademicPeriod period = AcademicPeriod.builder()
                    .name(dto.name())
                    .startDate(dto.startDate())
                    .endDate(dto.endDate())
                    .periodType(type)
                    .isCurrent(dto.isCurrent())
                    .build();
            academicPeriodRepository.save(period);
        }
    }

    @Transactional
    public int importUsers(List<UserImportDTO> dtos) {
        int created = 0;
        for (UserImportDTO dto : dtos) {
            try {
                // 1. Проверяем, не существует ли пользователь с таким email
                if (userRepository.findByEmail(dto.email()).isPresent()) {
                    System.err.println("Пользователь с email " + dto.email() + " уже существует, пропускаем");
                    continue;
                }

                // 2. Создаём пользователя
                User user = User.builder()
                        .firstName(dto.firstName())
                        .lastName(dto.lastName())
                        .middleName(dto.middleName())
                        .email(dto.email())
                        .phone(dto.phone())
                        .birthDate(dto.birthDate())
                        .status(dto.status() != null ? dto.status() : "PENDING")
                        .isStaff(dto.isStaff() != null ? dto.isStaff() : false)
                        .passwordHash(passwordEncoder.encode("changeMe123")) // временный пароль
                        .build();
                userRepository.save(user);
                System.out.println("Создан пользователь: " + user.getEmail());
                created++;

                // 3. Назначаем роль (если указана)
                if (dto.roleName() != null && !dto.roleName().isBlank()) {
                    Role role = roleRepository.findByName(dto.roleName())
                            .orElseThrow(() -> new RuntimeException("Роль " + dto.roleName() + " не найдена"));
                    UserRole userRole = UserRole.builder()
                            .user(user)
                            .role(role)
                            .build();
                    userRoleRepository.save(userRole);
                    System.out.println("Назначена роль " + dto.roleName() + " пользователю " + user.getEmail());
                }

                // 4. Если роль STUDENT и указан parentEmail – создаём связь
                if ("STUDENT".equalsIgnoreCase(dto.roleName()) && dto.parentEmail() != null && !dto.parentEmail().isBlank()) {
                    User parent = userRepository.findByEmail(dto.parentEmail()).orElse(null);
                    if (parent != null) {
                        // Убедимся, что у родителя есть роль PARENT
                        boolean parentHasRole = userRoleRepository.findByUserId(parent.getId()).stream()
                                .anyMatch(ur -> ur.getRole().getName().equals("PARENT"));
                        if (!parentHasRole) {
                            Role parentRole = roleRepository.findByName("PARENT")
                                    .orElseThrow(() -> new RuntimeException("Роль PARENT не найдена"));
                            UserRole parentUserRole = UserRole.builder()
                                    .user(parent)
                                    .role(parentRole)
                                    .build();
                            userRoleRepository.save(parentUserRole);
                        }
                        // Создаём связь
                        boolean relationExists = userRelationRepository.existsByParentUserIdAndChildUserId(parent.getId(), user.getId());
                        if (!relationExists) {
                            UserRelation relation = new UserRelation(parent.getId(), user.getId(), "parent", true);
                            userRelationRepository.save(relation);
                            System.out.println("Создана связь родитель-ребёнок: " + parent.getEmail() + " -> " + user.getEmail());
                        }
                    } else {
                        System.err.println("Родитель с email " + dto.parentEmail() + " не найден для студента " + user.getEmail());
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка при импорте пользователя " + dto.email() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return created;
    }

    @Transactional
    public void importPositions(List<PositionImportDTO> dtos) {
        for (PositionImportDTO dto : dtos) {
            // Проверяем, существует ли должность с таким именем
            if (positionRepository.findByName(dto.name()).isPresent()) {
                continue; // пропускаем – можно было бы обновить, но для простоты пропускаем
            }
            Position position = Position.builder()
                    .name(dto.name())
                    .hoursPerRate(dto.hoursPerRate())
                    .isTeaching(dto.isTeaching())
                    .build();
            positionRepository.save(position);
        }
    }

    @Transactional
    public void importStaff(List<StaffImportDTO> dtos) {
        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (StaffImportDTO dto : dtos) {
            try {
                // Находим пользователя по email
                User user = userRepository.findByEmail(dto.userEmail())
                        .orElse(null);

                if (user == null) {
                    System.err.println("Пользователь с email " + dto.userEmail() + " не найден, пропускаем");
                    skipped++;
                    continue;
                }

                // Находим должность по имени
                Position position = positionRepository.findByName(dto.positionName())
                        .orElse(null);

                if (position == null) {
                    System.err.println("Должность " + dto.positionName() + " не найдена, пропускаем");
                    skipped++;
                    continue;
                }

                // Проверяем, существует ли уже запись для этого сотрудника
                Optional<Staff> existingStaff = staffRepository.findByUserId(user.getId());

                if (existingStaff.isPresent()) {
                    // Обновляем существующего сотрудника
                    Staff staff = existingStaff.get();
                    staff.setPositionId(position.getId());
                    staff.setRateCount(dto.rateCount());
                    staff.setHireDate(dto.hireDate());
                    staff.setIsActive(dto.isActive());
                    if (Boolean.FALSE.equals(dto.isActive())) {
                        staff.setDismissalDate(LocalDate.now());
                    } else {
                        staff.setDismissalDate(null);
                    }
                    staffRepository.save(staff);
                    updated++;
                    System.out.println("Обновлён сотрудник: " + user.getEmail() + " -> " + position.getName());
                } else {
                    // Создаём нового сотрудника
                    Staff staff = Staff.builder()
                            .userId(user.getId())
                            .positionId(position.getId())
                            .rateCount(dto.rateCount())
                            .hireDate(dto.hireDate())
                            .isActive(dto.isActive())
                            .build();
                    staffRepository.save(staff);
                    created++;
                    System.out.println("Создан сотрудник: " + user.getEmail() + " -> " + position.getName());
                }
            } catch (Exception e) {
                System.err.println("Ошибка при импорте строки с email " + dto.userEmail() + ": " + e.getMessage());
                e.printStackTrace();
                skipped++;
            }
        }

        System.out.println("=== ИТОГИ ИМПОРТА ШТАТНОГО РАСПИСАНИЯ ===");
        System.out.println("  Создано: " + created);
        System.out.println("  Обновлено: " + updated);
        System.out.println("  Пропущено: " + skipped);
        System.out.println("  Всего обработано: " + dtos.size());
    }

    @Transactional
    public void importNews(List<NewsImportDTO> dtos) {
        for (NewsImportDTO dto : dtos) {
            User author = userRepository.findByEmail(dto.authorEmail())
                    .orElseThrow(() -> new RuntimeException("Пользователь с email " + dto.authorEmail() + " не найден"));
            News news = News.builder()
                    .title(dto.title())
                    .content(dto.content())
                    .author(author)
                    .isPublic(dto.isPublic() != null ? dto.isPublic() : false)
                    .pinned(dto.pinned() != null ? dto.pinned() : false)
                    .publishedAt(dto.publishedAt() != null ? dto.publishedAt() : OffsetDateTime.now())
                    .build();
            newsRepository.save(news);
        }
    }

    @Transactional
    public void importRooms(List<RoomImportDTO> dtos) {
        for (RoomImportDTO dto : dtos) {
            Room room = Room.builder()
                    .name(dto.name())
                    .type(RoomType.fromString(dto.type()))
                    .capacity(dto.capacity())
                    .build();
            roomRepository.save(room);
        }
    }

    @Transactional
    public void importProgramSubjects(List<ProgramSubjectImportDTO> dtos) {
        for (ProgramSubjectImportDTO dto : dtos) {
            Program program = programRepository.findByName(dto.programName())
                    .orElseThrow(() -> new RuntimeException("Программа не найдена: " + dto.programName()));
            Subject subject = subjectRepository.findByCode(dto.subjectCode())
                    .orElseThrow(() -> new RuntimeException("Предмет не найден: " + dto.subjectCode()));

            if (programSubjectRepository.existsByProgram_IdAndSubject_IdAndAcademicYear(
                    program.getId(), subject.getId(), dto.academicYear())) {
                continue;
            }

            BigDecimal hours = dto.hoursPerWeekForProgram().setScale(2, RoundingMode.HALF_UP);
            ProgramSubject ps = ProgramSubject.builder()
                    .program(program)
                    .subject(subject)
                    .academicYear(dto.academicYear())
                    .hoursPerWeekForProgram(hours)
                    .build();
            programSubjectRepository.save(ps);
        }
    }
}