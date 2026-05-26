package com.example.palitrum.controller;

import com.example.palitrum.dto.importDTO.*;
import com.example.palitrum.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.palitrum.dto.importDTO.StaffImportDTO;
import java.time.LocalDate;
import com.example.palitrum.dto.importDTO.UserImportDTO;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @GetMapping("/template")
    @PreAuthorize("hasAuthority('program.create')")
    public ResponseEntity<ByteArrayResource> downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            // --- Лист "Программы" ---
            Sheet sheetPrograms = workbook.createSheet("Программы");
            Row headerProg = sheetPrograms.createRow(0);
            headerProg.createCell(0).setCellValue("name");
            headerProg.createCell(1).setCellValue("description");
            headerProg.createCell(2).setCellValue("duration_years");
            Row exampleProg = sheetPrograms.createRow(1);
            exampleProg.createCell(0).setCellValue("Фортепиано");
            exampleProg.createCell(1).setCellValue("Начальный курс фортепиано");
            exampleProg.createCell(2).setCellValue(3);

            // --- Лист "Предметы" ---
            Sheet sheetSubjects = workbook.createSheet("Предметы");
            Row headerSubj = sheetSubjects.createRow(0);
            headerSubj.createCell(0).setCellValue("code");
            headerSubj.createCell(1).setCellValue("name");
            headerSubj.createCell(2).setCellValue("description");
            headerSubj.createCell(3).setCellValue("standard_hours_per_week");
            headerSubj.createCell(4).setCellValue("lesson_type");
            headerSubj.createCell(5).setCellValue("min_group_size");
            headerSubj.createCell(6).setCellValue("max_group_size");
            headerSubj.createCell(7).setCellValue("default_program_name");
            Row exampleSubj = sheetSubjects.createRow(1);
            exampleSubj.createCell(0).setCellValue("PIANO101");
            exampleSubj.createCell(1).setCellValue("Фортепиано");
            exampleSubj.createCell(2).setCellValue("Обучение игре на фортепиано");
            exampleSubj.createCell(3).setCellValue(2);
            exampleSubj.createCell(4).setCellValue("INDIVIDUAL");
            exampleSubj.createCell(5).setCellValue(1);
            exampleSubj.createCell(6).setCellValue(1);
            exampleSubj.createCell(7).setCellValue("Фортепиано");

            // --- Лист "Связи программ-предметов" ---
            Sheet sheetLinks = workbook.createSheet("Связи программ-предметов");
            Row headerLink = sheetLinks.createRow(0);
            headerLink.createCell(0).setCellValue("program_name");
            headerLink.createCell(1).setCellValue("subject_code");
            headerLink.createCell(2).setCellValue("academic_year");
            headerLink.createCell(3).setCellValue("hours_per_week_for_program");
            Row exampleLink = sheetLinks.createRow(1);
            exampleLink.createCell(0).setCellValue("Фортепиано");
            exampleLink.createCell(1).setCellValue("PIANO101");
            exampleLink.createCell(2).setCellValue(1);
            exampleLink.createCell(3).setCellValue(2);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона", e);
        }
    }

    // ================= Шаблон для пользователей =================
    @GetMapping("/users/template")
    @PreAuthorize("hasAuthority('user.create')")
    public ResponseEntity<ByteArrayResource> downloadUsersTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Пользователи");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("firstName");
            header.createCell(1).setCellValue("lastName");
            header.createCell(2).setCellValue("middleName");
            header.createCell(3).setCellValue("email");
            header.createCell(4).setCellValue("phone");
            header.createCell(5).setCellValue("birthDate (YYYY-MM-DD)");
            header.createCell(6).setCellValue("status (ACTIVE/PENDING/BLOCKED/ARCHIVED)");
            header.createCell(7).setCellValue("isStaff (true/false)");
            header.createCell(8).setCellValue("roleName (STUDENT/TEACHER/PARENT/MANAGER/ADMIN)");
            header.createCell(9).setCellValue("parentEmail (только для STUDENT)");

            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("Иван");
            example.createCell(1).setCellValue("Петров");
            example.createCell(2).setCellValue("Алексеевич");
            example.createCell(3).setCellValue("student@example.com");
            example.createCell(4).setCellValue("+79991234567");
            example.createCell(5).setCellValue("2010-05-15");
            example.createCell(6).setCellValue("ACTIVE");
            example.createCell(7).setCellValue("false");
            example.createCell(8).setCellValue("STUDENT");
            example.createCell(9).setCellValue("parent@example.com");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона пользователей", e);
        }
    }

    // Импорт пользователей
    @PostMapping("/users/excel")
    @PreAuthorize("hasAuthority('user.create')")
    public ResponseEntity<?> importUsersExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Пользователи");
            if (sheet == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Пользователи' не найден. Убедитесь, что в файле есть лист с таким названием."));
            }

            List<UserImportDTO> users = new ArrayList<>();
            int skipped = 0;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;

                try {
                    String firstName = getCellString(row.getCell(0));
                    if (firstName == null || firstName.isBlank()) {
                        skipped++;
                        continue;
                    }
                    String lastName = getCellString(row.getCell(1));
                    String middleName = getCellString(row.getCell(2));
                    String email = getCellString(row.getCell(3));
                    if (email == null || email.isBlank()) {
                        skipped++;
                        continue;
                    }
                    String phone = getCellString(row.getCell(4));
                    String birthDateStr = getCellString(row.getCell(5));
                    LocalDate birthDate = null;
                    try {
                        if (birthDateStr != null && !birthDateStr.isBlank())
                            birthDate = LocalDate.parse(birthDateStr);
                    } catch (Exception e) {}
                    String status = getCellString(row.getCell(6));
                    if (status == null || status.isBlank()) status = "PENDING";
                    String isStaffStr = getCellString(row.getCell(7));
                    Boolean isStaff = "true".equalsIgnoreCase(isStaffStr);
                    String roleName = getCellString(row.getCell(8));
                    String parentEmail = getCellString(row.getCell(9));

                    users.add(new UserImportDTO(firstName, lastName, middleName, email, phone, birthDate,
                            status, isStaff, roleName, parentEmail));
                } catch (Exception e) {
                    skipped++;
                }
            }

            if (users.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "В файле нет корректных строк для импорта. Проверьте данные."));
            }

            int created = importService.importUsers(users);
            return ResponseEntity.ok(Map.of(
                    "message", "Импорт пользователей выполнен успешно",
                    "created", created,
                    "skipped", skipped
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    // ================= Шаблон для штатного расписания =================
    @GetMapping("/staff/template")
    @PreAuthorize("hasAuthority('staff.create')")
    public ResponseEntity<ByteArrayResource> downloadStaffTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Штатное расписание");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("userEmail");
            header.createCell(1).setCellValue("positionName");
            header.createCell(2).setCellValue("rateCount");
            header.createCell(3).setCellValue("hireDate (YYYY-MM-DD)");
            header.createCell(4).setCellValue("isActive (true/false)");

            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("teacher@example.com");
            example.createCell(1).setCellValue("Преподаватель фортепиано");
            example.createCell(2).setCellValue(1.0);
            example.createCell(3).setCellValue("2025-01-15");
            example.createCell(4).setCellValue("true");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=staff_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона штатного расписания", e);
        }
    }

    // ================= Импорт штатного расписания из Excel =================
    @PostMapping("/staff/excel")
    @PreAuthorize("hasAuthority('staff.create')")
    public ResponseEntity<?> importStaffExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Штатное расписание");
            if (sheet == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Штатное расписание' не найден"));
            }

            List<StaffImportDTO> staffList = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String userEmail = getCellString(row.getCell(0));
                if (userEmail == null || userEmail.isBlank()) continue;
                String positionName = getCellString(row.getCell(1));

                Cell rateCell = row.getCell(2);
                if (rateCell == null || rateCell.getCellType() != CellType.NUMERIC) {
                    System.err.println("Пропущена строка " + (row.getRowNum() + 1) + ": некорректное значение rateCount");
                    continue;
                }
                double rateCountDouble = rateCell.getNumericCellValue();
                BigDecimal rateCount = BigDecimal.valueOf(rateCountDouble);

                String hireDateStr = getCellString(row.getCell(3));
                LocalDate hireDate = LocalDate.parse(hireDateStr);
                String isActiveStr = getCellString(row.getCell(4));
                Boolean isActive = "true".equalsIgnoreCase(isActiveStr);

                staffList.add(new StaffImportDTO(userEmail, positionName, rateCount, hireDate, isActive));
            }

            importService.importStaff(staffList);
            return ResponseEntity.ok(Map.of("message", "Импорт штатного расписания выполнен успешно"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    // ================= Шаблон для отделений =================
    @GetMapping("/departments/template")
    @PreAuthorize("hasAuthority('department.create')")
    public ResponseEntity<ByteArrayResource> downloadDepartmentsTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Отделения");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("description");
            header.createCell(2).setCellValue("parent_name");
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("Музыкальное отделение");
            example.createCell(1).setCellValue("Обучение музыке");
            example.createCell(2).setCellValue("");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=departments_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона отделений", e);
        }
    }

    // ================= Шаблон для должностей =================
    @GetMapping("/positions/template")
    @PreAuthorize("hasAuthority('position.create')")
    public ResponseEntity<ByteArrayResource> downloadPositionsTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Должности");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("hours_per_rate");
            header.createCell(2).setCellValue("is_teaching (true/false)");

            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("Преподаватель фортепиано");
            example.createCell(1).setCellValue(24);
            example.createCell(2).setCellValue("true");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=positions_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона должностей", e);
        }
    }

    // ================= Импорт должностей из Excel =================
    @PostMapping("/positions/excel")
    @PreAuthorize("hasAuthority('position.create')")
    public ResponseEntity<?> importPositionsExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Должности");
            if (sheet == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Должности' не найден"));
            }

            List<PositionImportDTO> positions = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String name = getCellString(row.getCell(0));
                if (name == null || name.isBlank()) continue;
                BigDecimal hoursPerRate = null;
                if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC) {
                    hoursPerRate = BigDecimal.valueOf(row.getCell(1).getNumericCellValue());
                }
                String isTeachingStr = getCellString(row.getCell(2));
                Boolean isTeaching = "true".equalsIgnoreCase(isTeachingStr);
                positions.add(new PositionImportDTO(name, hoursPerRate, isTeaching));
            }

            importService.importPositions(positions);
            return ResponseEntity.ok(Map.of("message", "Импорт должностей выполнен успешно"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    // ================= МАССОВЫЙ ИМПОРТ (ВСЕ ТАБЛИЦЫ) =================
    @GetMapping("/departments-programs/template")
    @PreAuthorize("hasAuthority('department.create')")
    public ResponseEntity<ByteArrayResource> downloadBulkImportTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {

            // 1. Отделения
            Sheet sheetDepts = workbook.createSheet("Отделения");
            Row headerDept = sheetDepts.createRow(0);
            headerDept.createCell(0).setCellValue("name");
            headerDept.createCell(1).setCellValue("description");
            headerDept.createCell(2).setCellValue("parent_name");
            Row exampleDept = sheetDepts.createRow(1);
            exampleDept.createCell(0).setCellValue("Музыкальное отделение");
            exampleDept.createCell(1).setCellValue("Обучение музыке");
            exampleDept.createCell(2).setCellValue("");

            // 2. Программы
            Sheet sheetProgs = workbook.createSheet("Программы");
            Row headerProg = sheetProgs.createRow(0);
            headerProg.createCell(0).setCellValue("name");
            headerProg.createCell(1).setCellValue("description");
            headerProg.createCell(2).setCellValue("duration_years");
            Row exampleProg = sheetProgs.createRow(1);
            exampleProg.createCell(0).setCellValue("Фортепиано");
            exampleProg.createCell(1).setCellValue("Обучение игре на фортепиано");
            exampleProg.createCell(2).setCellValue(4);

            // 3. Предметы
            Sheet sheetSubjects = workbook.createSheet("Предметы");
            Row headerSubj = sheetSubjects.createRow(0);
            headerSubj.createCell(0).setCellValue("code");
            headerSubj.createCell(1).setCellValue("name");
            headerSubj.createCell(2).setCellValue("description");
            headerSubj.createCell(3).setCellValue("standard_hours_per_week");
            headerSubj.createCell(4).setCellValue("lesson_type");
            headerSubj.createCell(5).setCellValue("min_group_size");
            headerSubj.createCell(6).setCellValue("max_group_size");
            headerSubj.createCell(7).setCellValue("default_program_name");
            Row exampleSubj = sheetSubjects.createRow(1);
            exampleSubj.createCell(0).setCellValue("PIANO101");
            exampleSubj.createCell(1).setCellValue("Фортепиано");
            exampleSubj.createCell(2).setCellValue("Обучение игре на фортепиано");
            exampleSubj.createCell(3).setCellValue(2);
            exampleSubj.createCell(4).setCellValue("INDIVIDUAL");
            exampleSubj.createCell(5).setCellValue(1);
            exampleSubj.createCell(6).setCellValue(1);
            exampleSubj.createCell(7).setCellValue("Фортепиано");

            // 4. Связи программ-предметов
            Sheet sheetLinks = workbook.createSheet("Связи программ-предметов");
            Row headerLink = sheetLinks.createRow(0);
            headerLink.createCell(0).setCellValue("program_name");
            headerLink.createCell(1).setCellValue("subject_code");
            headerLink.createCell(2).setCellValue("academic_year");
            headerLink.createCell(3).setCellValue("hours_per_week_for_program");
            Row exampleLink = sheetLinks.createRow(1);
            exampleLink.createCell(0).setCellValue("Фортепиано");
            exampleLink.createCell(1).setCellValue("PIANO101");
            exampleLink.createCell(2).setCellValue(1);
            exampleLink.createCell(3).setCellValue(2);

            // 5. Пользователи
            Sheet sheetUsers = workbook.createSheet("Пользователи");
            Row headerUsers = sheetUsers.createRow(0);
            headerUsers.createCell(0).setCellValue("firstName");
            headerUsers.createCell(1).setCellValue("lastName");
            headerUsers.createCell(2).setCellValue("middleName");
            headerUsers.createCell(3).setCellValue("email");
            headerUsers.createCell(4).setCellValue("phone");
            headerUsers.createCell(5).setCellValue("birthDate (YYYY-MM-DD)");
            headerUsers.createCell(6).setCellValue("status (ACTIVE/PENDING/BLOCKED/ARCHIVED)");
            headerUsers.createCell(7).setCellValue("isStaff (true/false)");
            headerUsers.createCell(8).setCellValue("roleName (STUDENT/TEACHER/PARENT/MANAGER/ADMIN)");
            headerUsers.createCell(9).setCellValue("parentEmail (только для STUDENT)");
            Row exampleUser = sheetUsers.createRow(1);
            exampleUser.createCell(0).setCellValue("Иван");
            exampleUser.createCell(1).setCellValue("Петров");
            exampleUser.createCell(2).setCellValue("Алексеевич");
            exampleUser.createCell(3).setCellValue("student@example.com");
            exampleUser.createCell(4).setCellValue("+79991234567");
            exampleUser.createCell(5).setCellValue("2010-05-15");
            exampleUser.createCell(6).setCellValue("ACTIVE");
            exampleUser.createCell(7).setCellValue("false");
            exampleUser.createCell(8).setCellValue("STUDENT");
            exampleUser.createCell(9).setCellValue("parent@example.com");

            // 6. Должности
            Sheet sheetPositions = workbook.createSheet("Должности");
            Row headerPositions = sheetPositions.createRow(0);
            headerPositions.createCell(0).setCellValue("name");
            headerPositions.createCell(1).setCellValue("hours_per_rate");
            headerPositions.createCell(2).setCellValue("is_teaching (true/false)");
            Row examplePosition = sheetPositions.createRow(1);
            examplePosition.createCell(0).setCellValue("Преподаватель фортепиано");
            examplePosition.createCell(1).setCellValue(24);
            examplePosition.createCell(2).setCellValue("true");

            // 7. Штатное расписание
            Sheet sheetStaff = workbook.createSheet("Штатное расписание");
            Row headerStaff = sheetStaff.createRow(0);
            headerStaff.createCell(0).setCellValue("userEmail");
            headerStaff.createCell(1).setCellValue("positionName");
            headerStaff.createCell(2).setCellValue("rateCount");
            headerStaff.createCell(3).setCellValue("hireDate (YYYY-MM-DD)");
            headerStaff.createCell(4).setCellValue("isActive (true/false)");
            Row exampleStaff = sheetStaff.createRow(1);
            exampleStaff.createCell(0).setCellValue("teacher@example.com");
            exampleStaff.createCell(1).setCellValue("Преподаватель фортепиано");
            exampleStaff.createCell(2).setCellValue(1.0);
            exampleStaff.createCell(3).setCellValue("2025-01-15");
            exampleStaff.createCell(4).setCellValue("true");

            // 8. Помещения
            Sheet sheetRooms = workbook.createSheet("Помещения");
            Row headerRooms = sheetRooms.createRow(0);
            headerRooms.createCell(0).setCellValue("name");
            headerRooms.createCell(1).setCellValue("type");
            headerRooms.createCell(2).setCellValue("capacity");
            Row exampleRoom = sheetRooms.createRow(1);
            exampleRoom.createCell(0).setCellValue("Актовый зал");
            exampleRoom.createCell(1).setCellValue("hall");
            exampleRoom.createCell(2).setCellValue(100);

            // 9. Учебные периоды
            Sheet sheetPeriods = workbook.createSheet("Учебные периоды");
            Row headerPeriods = sheetPeriods.createRow(0);
            headerPeriods.createCell(0).setCellValue("name");
            headerPeriods.createCell(1).setCellValue("startDate (YYYY-MM-DD)");
            headerPeriods.createCell(2).setCellValue("endDate (YYYY-MM-DD)");
            headerPeriods.createCell(3).setCellValue("periodType (SEMESTER/QUARTER/YEAR)");
            headerPeriods.createCell(4).setCellValue("isCurrent (true/false)");
            Row examplePeriod = sheetPeriods.createRow(1);
            examplePeriod.createCell(0).setCellValue("Осенний семестр 2025");
            examplePeriod.createCell(1).setCellValue("2025-09-01");
            examplePeriod.createCell(2).setCellValue("2025-12-31");
            examplePeriod.createCell(3).setCellValue("SEMESTER");
            examplePeriod.createCell(4).setCellValue("true");

            // 10. Новости
            Sheet sheetNews = workbook.createSheet("Новости");
            Row headerNews = sheetNews.createRow(0);
            headerNews.createCell(0).setCellValue("title");
            headerNews.createCell(1).setCellValue("content");
            headerNews.createCell(2).setCellValue("authorEmail");
            headerNews.createCell(3).setCellValue("isPublic (true/false)");
            headerNews.createCell(4).setCellValue("pinned (true/false)");
            headerNews.createCell(5).setCellValue("publishedAt (yyyy-MM-dd HH:mm:ss)");
            Row exampleNews = sheetNews.createRow(1);
            exampleNews.createCell(0).setCellValue("Открытие нового класса");
            exampleNews.createCell(1).setCellValue("Приглашаем всех на торжественное открытие...");
            exampleNews.createCell(2).setCellValue("admin@example.com");
            exampleNews.createCell(3).setCellValue("true");
            exampleNews.createCell(4).setCellValue("true");
            exampleNews.createCell(5).setCellValue("2025-09-01 10:00:00");

            // 11. Связи отделений-программ
            Sheet sheetDeptProgLinks = workbook.createSheet("Связи отделений-программ");
            Row headerDeptProgLink = sheetDeptProgLinks.createRow(0);
            headerDeptProgLink.createCell(0).setCellValue("department_name");
            headerDeptProgLink.createCell(1).setCellValue("program_name");
            headerDeptProgLink.createCell(2).setCellValue("is_primary");
            headerDeptProgLink.createCell(3).setCellValue("notes");
            Row exampleDeptProgLink = sheetDeptProgLinks.createRow(1);
            exampleDeptProgLink.createCell(0).setCellValue("Музыкальное отделение");
            exampleDeptProgLink.createCell(1).setCellValue("Фортепиано");
            exampleDeptProgLink.createCell(2).setCellValue("true");
            exampleDeptProgLink.createCell(3).setCellValue("Основное направление");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bulk_import_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании общего шаблона импорта", e);
        }
    }

    // ================= Импорт отделений, программ и связей из Excel (МАССОВЫЙ ИМПОРТ) =================
    @PostMapping("/departments-programs/excel")
    @PreAuthorize("hasAuthority('department.create')")
    public ResponseEntity<?> importDepartmentsProgramsExcel(@RequestParam("file") MultipartFile file) {
        System.out.println("=== НАЧАЛО ИМПОРТА ===");
        System.out.println("Имя файла: " + file.getOriginalFilename());
        System.out.println("Размер файла: " + file.getSize());

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            // Логируем все листы для отладки
            System.out.println("=== Листы в загруженном файле ===");
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                System.out.println("- " + workbook.getSheetName(i));
            }

            // 1. Лист "Отделения"
            Sheet sheetDepts = workbook.getSheet("Отделения");
            if (sheetDepts == null) {
                System.err.println("ERROR: Лист 'Отделения' не найден!");
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Отделения' не найден"));
            }

            List<DepartmentImportDTO> departments = new ArrayList<>();
            System.out.println("=== Чтение отделений ===");
            for (Row row : sheetDepts) {
                if (row.getRowNum() == 0) {
                    System.out.println("Заголовки: " +
                            getCellString(row.getCell(0)) + " | " +
                            getCellString(row.getCell(1)) + " | " +
                            getCellString(row.getCell(2)));
                    continue;
                }
                String name = getCellString(row.getCell(0));
                if (name == null || name.isBlank()) {
                    System.out.println("Пропущена строка " + (row.getRowNum() + 1) + ": пустое имя");
                    continue;
                }
                String description = getCellString(row.getCell(1));
                String parentName = getCellString(row.getCell(2));
                departments.add(new DepartmentImportDTO(name, description, parentName));
                System.out.println("  Отделение: '" + name + "', parent='" + parentName + "'");
            }
            System.out.println("Итого отделений: " + departments.size());

            // 2. Лист "Программы"
            Sheet sheetProgs = workbook.getSheet("Программы");
            if (sheetProgs == null) {
                System.err.println("ERROR: Лист 'Программы' не найден!");
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Программы' не найден"));
            }

            List<ProgramImportDTO> programs = new ArrayList<>();
            System.out.println("=== Чтение программ ===");
            for (Row row : sheetProgs) {
                if (row.getRowNum() == 0) {
                    System.out.println("Заголовки: " +
                            getCellString(row.getCell(0)) + " | " +
                            getCellString(row.getCell(1)) + " | " +
                            getCellString(row.getCell(2)));
                    continue;
                }
                String name = getCellString(row.getCell(0));
                if (name == null || name.isBlank()) {
                    System.out.println("Пропущена строка " + (row.getRowNum() + 1) + ": пустое имя");
                    continue;
                }
                String description = getCellString(row.getCell(1));
                Integer duration = null;
                if (row.getCell(2) != null && row.getCell(2).getCellType() == CellType.NUMERIC) {
                    duration = (int) row.getCell(2).getNumericCellValue();
                }
                programs.add(new ProgramImportDTO(name, description, duration));
                System.out.println("  Программа: '" + name + "', duration=" + duration);
            }
            System.out.println("Итого программ: " + programs.size());

            // 3. Лист связей
            Sheet sheetLinks = workbook.getSheet("Связи отделений-программ");
            if (sheetLinks == null) {
                sheetLinks = workbook.getSheet("Связи");
                if (sheetLinks != null) {
                    System.out.println("Найден лист 'Связи' (старое название)");
                }
            }

            List<DepartmentProgramLinkDTO> links = new ArrayList<>();
            if (sheetLinks != null) {
                System.out.println("=== Чтение связей ===");
                System.out.println("Всего строк в листе связей: " + (sheetLinks.getLastRowNum() + 1));

                for (Row row : sheetLinks) {
                    if (row.getRowNum() == 0) {
                        System.out.println("Заголовки связей: " +
                                getCellString(row.getCell(0)) + " | " +
                                getCellString(row.getCell(1)) + " | " +
                                getCellString(row.getCell(2)) + " | " +
                                getCellString(row.getCell(3)));
                        continue;
                    }

                    String deptName = getCellString(row.getCell(0));
                    if (deptName == null || deptName.isBlank()) {
                        System.out.println("  Пропущена строка " + (row.getRowNum() + 1) + ": пустое отделение");
                        continue;
                    }

                    String progName = getCellString(row.getCell(1));
                    if (progName == null || progName.isBlank()) {
                        System.out.println("  Пропущена строка " + (row.getRowNum() + 1) + ": пустая программа");
                        continue;
                    }

                    // Чтение isPrimary
                    Boolean isPrimary = false;
                    Cell primaryCell = row.getCell(2);
                    if (primaryCell != null) {
                        if (primaryCell.getCellType() == CellType.BOOLEAN) {
                            isPrimary = primaryCell.getBooleanCellValue();
                        } else if (primaryCell.getCellType() == CellType.STRING) {
                            String val = primaryCell.getStringCellValue();
                            isPrimary = "true".equalsIgnoreCase(val) || "да".equalsIgnoreCase(val) || "1".equals(val);
                        } else if (primaryCell.getCellType() == CellType.NUMERIC) {
                            isPrimary = primaryCell.getNumericCellValue() == 1.0;
                        }
                    }

                    String notes = getCellString(row.getCell(3));
                    links.add(new DepartmentProgramLinkDTO(deptName, progName, isPrimary, notes));
                    System.out.println("  Связь: '" + deptName + "' -> '" + progName + "', isPrimary=" + isPrimary);
                }
                System.out.println("Итого связей: " + links.size());
            } else {
                System.err.println("WARNING: Лист со связями не найден!");
            }

            // Вызываем сервис
            System.out.println("=== Вызов сервиса импорта ===");
            importService.importDepartmentsPrograms(departments, programs, links);

            System.out.println("=== ИМПОРТ УСПЕШНО ЗАВЕРШЕН ===");

            return ResponseEntity.ok(Map.of(
                    "message", "Импорт выполнен успешно",
                    "departments", departments.size(),
                    "programs", programs.size(),
                    "links", links.size()
            ));
        } catch (Exception e) {
            System.err.println("=== ОШИБКА ИМПОРТА ===");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    // ================= Импорт отделений из Excel =================
    @PostMapping("/departments/excel")
    @PreAuthorize("hasAuthority('department.create')")
    public ResponseEntity<?> importDepartmentsExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Отделения");
            if (sheet == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Отделения' не найден"));
            }

            List<DepartmentImportDTO> departments = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String name = getCellString(row.getCell(0));
                if (name == null || name.isBlank()) continue;
                String description = getCellString(row.getCell(1));
                String parentName = getCellString(row.getCell(2));
                departments.add(new DepartmentImportDTO(name, description, parentName));
            }

            importService.importDepartments(departments);
            return ResponseEntity.ok(Map.of("message", "Импорт отделений выполнен успешно"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    // ================= Шаблон для помещений =================
    @GetMapping("/rooms/template")
    @PreAuthorize("hasAuthority('room.create')")
    public ResponseEntity<ByteArrayResource> downloadRoomsTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Помещения");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("type");
            header.createCell(2).setCellValue("capacity");
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("Актовый зал");
            example.createCell(1).setCellValue("hall");
            example.createCell(2).setCellValue(100);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rooms_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона помещений", e);
        }
    }

    // ================= Импорт помещений из Excel =================
    @PostMapping("/rooms/excel")
    @PreAuthorize("hasAuthority('room.create')")
    public ResponseEntity<?> importRoomsExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Помещения");
            if (sheet == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Помещения' не найден"));
            }

            List<RoomImportDTO> rooms = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String name = getCellString(row.getCell(0));
                if (name == null || name.isBlank()) continue;
                String type = getCellString(row.getCell(1));
                Cell capacityCell = row.getCell(2);
                if (capacityCell == null || capacityCell.getCellType() != CellType.NUMERIC) {
                    System.err.println("Пропущена строка " + (row.getRowNum() + 1) + ": некорректное значение capacity");
                    continue;
                }
                int capacity = (int) capacityCell.getNumericCellValue();
                rooms.add(new RoomImportDTO(name, type, capacity));
            }

            importService.importRooms(rooms);
            return ResponseEntity.ok(Map.of("message", "Импорт помещений выполнен успешно", "imported", rooms.size()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    @PostMapping("/excel")
    @PreAuthorize("hasAuthority('program.create')")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            // --- Лист "Программы" ---
            Sheet sheetPrograms = workbook.getSheet("Программы");
            if (sheetPrograms != null) {
                List<ProgramImportDTO> programs = new ArrayList<>();
                for (Row row : sheetPrograms) {
                    if (row.getRowNum() == 0) continue;
                    String name = getCellString(row.getCell(0));
                    if (name == null || name.isBlank()) continue;
                    String description = getCellString(row.getCell(1));
                    int duration = (int) row.getCell(2).getNumericCellValue();
                    programs.add(new ProgramImportDTO(name, description, duration));
                }
                importService.importPrograms(programs);
            }

            // --- Лист "Предметы" ---
            Sheet sheetSubjects = workbook.getSheet("Предметы");
            if (sheetSubjects != null) {
                List<SubjectImportDTO> subjects = new ArrayList<>();
                for (Row row : sheetSubjects) {
                    if (row.getRowNum() == 0) continue;
                    String code = getCellString(row.getCell(0));
                    if (code == null || code.isBlank()) continue;
                    String name = getCellString(row.getCell(1));
                    String description = getCellString(row.getCell(2));
                    double hoursDouble = row.getCell(3).getNumericCellValue();
                    int standardHours = (int) Math.round(hoursDouble);
                    String lessonType = getCellString(row.getCell(4));
                    int minSize = (int) row.getCell(5).getNumericCellValue();
                    int maxSize = (int) row.getCell(6).getNumericCellValue();
                    String defaultProg = getCellString(row.getCell(7));
                    subjects.add(new SubjectImportDTO(code, name, description, standardHours, lessonType, minSize, maxSize, defaultProg));
                }
                importService.importSubjects(subjects);
            }

            // --- Лист "Связи программ-предметов" ---
            Sheet sheetLinks = workbook.getSheet("Связи программ-предметов");
            if (sheetLinks != null) {
                List<ProgramSubjectImportDTO> links = new ArrayList<>();
                for (Row row : sheetLinks) {
                    if (row.getRowNum() == 0) continue;
                    String progName = getCellString(row.getCell(0));
                    if (progName == null || progName.isBlank()) continue;
                    String subjCode = getCellString(row.getCell(1));
                    int year = (int) row.getCell(2).getNumericCellValue();
                    double rawHours = row.getCell(3).getNumericCellValue();
                    BigDecimal hours = BigDecimal.valueOf(rawHours).setScale(2, RoundingMode.HALF_UP);
                    links.add(new ProgramSubjectImportDTO(progName, subjCode, year, hours));
                }
                importService.importProgramSubjects(links);
            }

            return ResponseEntity.ok(Map.of("message", "Импорт выполнен успешно"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    // ================= Шаблон для предметов =================
    @GetMapping("/subjects/template")
    @PreAuthorize("hasAuthority('subject.create')")
    public ResponseEntity<ByteArrayResource> downloadSubjectsTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Предметы");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("code");
            header.createCell(1).setCellValue("name");
            header.createCell(2).setCellValue("description");
            header.createCell(3).setCellValue("standard_hours_per_week");
            header.createCell(4).setCellValue("lesson_type");
            header.createCell(5).setCellValue("min_group_size");
            header.createCell(6).setCellValue("max_group_size");
            header.createCell(7).setCellValue("default_program_name");
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("PIANO101");
            example.createCell(1).setCellValue("Фортепиано");
            example.createCell(2).setCellValue("Обучение игре на фортепиано");
            example.createCell(3).setCellValue(2);
            example.createCell(4).setCellValue("INDIVIDUAL");
            example.createCell(5).setCellValue(1);
            example.createCell(6).setCellValue(1);
            example.createCell(7).setCellValue("Фортепиано");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subjects_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона предметов", e);
        }
    }

    // ================= Импорт предметов из Excel =================
    @PostMapping("/subjects/excel")
    @PreAuthorize("hasAuthority('subject.create')")
    public ResponseEntity<?> importSubjectsExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Предметы");
            if (sheet == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Предметы' не найден"));
            }
            List<SubjectImportDTO> subjects = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String code = getCellString(row.getCell(0));
                if (code == null || code.isBlank()) continue;
                String name = getCellString(row.getCell(1));
                String description = getCellString(row.getCell(2));
                double hoursDouble = row.getCell(3).getNumericCellValue();
                int standardHours = (int) Math.round(hoursDouble);
                String lessonType = getCellString(row.getCell(4));
                int minSize = (int) row.getCell(5).getNumericCellValue();
                int maxSize = (int) row.getCell(6).getNumericCellValue();
                String defaultProg = getCellString(row.getCell(7));
                subjects.add(new SubjectImportDTO(code, name, description, standardHours, lessonType, minSize, maxSize, defaultProg));
            }
            importService.importSubjects(subjects);
            return ResponseEntity.ok(Map.of("message", "Импорт предметов выполнен успешно", "imported", subjects.size()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    // ================= Шаблон для новостей =================
    @GetMapping("/news/template")
    @PreAuthorize("hasAuthority('news.create')")
    public ResponseEntity<ByteArrayResource> downloadNewsTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Новости");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("title");
            header.createCell(1).setCellValue("content");
            header.createCell(2).setCellValue("authorEmail");
            header.createCell(3).setCellValue("isPublic (true/false)");
            header.createCell(4).setCellValue("pinned (true/false)");
            header.createCell(5).setCellValue("publishedAt (yyyy-MM-dd HH:mm:ss)");
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("Открытие нового класса");
            example.createCell(1).setCellValue("Приглашаем всех на торжественное открытие...");
            example.createCell(2).setCellValue("admin@example.com");
            example.createCell(3).setCellValue("true");
            example.createCell(4).setCellValue("true");
            example.createCell(5).setCellValue("2025-09-01 10:00:00");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=news_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона новостей", e);
        }
    }

    // ================= Шаблон для учебных периодов =================
    @GetMapping("/academic-periods/template")
    @PreAuthorize("hasAuthority('academic_period.create')")
    public ResponseEntity<ByteArrayResource> downloadAcademicPeriodsTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Учебные периоды");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("startDate (YYYY-MM-DD)");
            header.createCell(2).setCellValue("endDate (YYYY-MM-DD)");
            header.createCell(3).setCellValue("periodType (SEMESTER/QUARTER/YEAR)");
            header.createCell(4).setCellValue("isCurrent (true/false)");

            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("Осенний семестр 2025");
            example.createCell(1).setCellValue("2025-09-01");
            example.createCell(2).setCellValue("2025-12-31");
            example.createCell(3).setCellValue("SEMESTER");
            example.createCell(4).setCellValue("true");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=academic_periods_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании шаблона учебных периодов", e);
        }
    }

    // ================= Импорт учебных периодов из Excel =================
    // ================= Импорт учебных периодов из Excel =================
    @PostMapping("/academic-periods/excel")
    @PreAuthorize("hasAuthority('academic_period.create')")
    public ResponseEntity<?> importAcademicPeriodsExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Учебные периоды");
            if (sheet == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Учебные периоды' не найден"));
            }

            List<AcademicPeriodImportDTO> periods = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String name = getCellString(row.getCell(0));
                if (name == null || name.isBlank()) continue;

                LocalDate startDate = getLocalDateFromCell(row.getCell(1));
                LocalDate endDate = getLocalDateFromCell(row.getCell(2));

                if (startDate == null || endDate == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Неверный формат даты в строке " + (row.getRowNum() + 1)));
                }

                String periodType = getCellString(row.getCell(3));
                if (periodType == null || periodType.isBlank()) {
                    periodType = "SEMESTER";
                }

                Boolean isCurrent = false;
                Cell currentCell = row.getCell(4);
                if (currentCell != null) {
                    if (currentCell.getCellType() == CellType.BOOLEAN) {
                        isCurrent = currentCell.getBooleanCellValue();
                    } else if (currentCell.getCellType() == CellType.STRING) {
                        String val = currentCell.getStringCellValue();
                        isCurrent = "true".equalsIgnoreCase(val) || "да".equalsIgnoreCase(val) || "1".equals(val);
                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
                        isCurrent = currentCell.getNumericCellValue() == 1.0;
                    }
                }

                periods.add(new AcademicPeriodImportDTO(name, startDate, endDate, periodType, isCurrent));
            }

            importService.importAcademicPeriods(periods);
            return ResponseEntity.ok(Map.of("message", "Импорт учебных периодов выполнен успешно", "imported", periods.size()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    // Добавьте этот вспомогательный метод в класс ImportController
    private LocalDate getLocalDateFromCell(Cell cell) {
        if (cell == null) return null;

        try {
            switch (cell.getCellType()) {
                case STRING:
                    String dateStr = cell.getStringCellValue().trim();
                    if (dateStr.isEmpty()) return null;
                    return LocalDate.parse(dateStr);

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue().toLocalDate();
                    } else {
                        // Excel хранит даты как число дней с 1900-01-01
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue >= 1 && numericValue <= 100000) {
                            // Конвертируем Excel serial date в LocalDate
                            java.time.LocalDate baseDate = java.time.LocalDate.of(1900, 1, 1);
                            int daysToAdd = (int) numericValue - 2; // -2 потому что Excel считает 1900 год високосным
                            return baseDate.plusDays(daysToAdd);
                        }
                        return null;
                    }

                case FORMULA:
                    return getLocalDateFromCell(cell);

                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга даты: " + e.getMessage());
            return null;
        }
    }

    // ================= Импорт новостей из Excel =================
    @PostMapping("/news/excel")
    @PreAuthorize("hasAuthority('news.create')")
    public ResponseEntity<?> importNewsExcel(@RequestParam("file") MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Новости");
            if (sheet == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Лист 'Новости' не найден"));
            }

            List<NewsImportDTO> newsList = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String title = getCellString(row.getCell(0));
                if (title == null || title.isBlank()) continue;
                String content = getCellString(row.getCell(1));
                String authorEmail = getCellString(row.getCell(2));
                String isPublicStr = getCellString(row.getCell(3));
                String pinnedStr = getCellString(row.getCell(4));
                OffsetDateTime publishedAt = null;
                try {
                    Cell pubCell = row.getCell(5);
                    if (pubCell != null && pubCell.getCellType() == CellType.NUMERIC) {
                        publishedAt = pubCell.getLocalDateTimeCellValue().atOffset(ZoneOffset.UTC);
                    } else if (pubCell != null) {
                        publishedAt = OffsetDateTime.parse(getCellString(pubCell));
                    }
                } catch (Exception ignored) {}
                Boolean isPublic = "true".equalsIgnoreCase(isPublicStr);
                Boolean pinned = "true".equalsIgnoreCase(pinnedStr);
                newsList.add(new NewsImportDTO(title, content, authorEmail, isPublic, pinned, publishedAt));
            }

            importService.importNews(newsList);
            return ResponseEntity.ok(Map.of("message", "Импорт новостей выполнен успешно", "imported", newsList.size()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    yield String.valueOf((long) val);
                } else {
                    yield String.valueOf(val);
                }
            }
            case FORMULA -> getCellString(cell);
            default -> null;
        };
    }
}