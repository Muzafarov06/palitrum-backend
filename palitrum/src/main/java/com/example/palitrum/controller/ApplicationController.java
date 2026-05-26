package com.example.palitrum.controller;

import com.example.palitrum.dto.ApplicationCreateDto;
import com.example.palitrum.dto.ApplicationResponseDto;
import com.example.palitrum.dto.FileCreateDto;
import com.example.palitrum.model.Application;
import com.example.palitrum.repository.ApplicationRepository;
import com.example.palitrum.service.ApplicationService;
import com.example.palitrum.service.EnrollmentService;
import com.example.palitrum.service.FilesService;
import com.example.palitrum.service.FileStorageService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService service;
    private final FilesService filesService;
    private final FileStorageService storageService;
    private final EnrollmentService enrollmentService;
    private final ApplicationRepository applicationRepository; // добавлен
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public ApplicationController(ApplicationService service,
                                 FilesService filesService,
                                 FileStorageService storageService,
                                 EnrollmentService enrollmentService,
                                 ApplicationRepository applicationRepository) {
        this.service = service;
        this.filesService = filesService;
        this.storageService = storageService;
        this.enrollmentService = enrollmentService;
        this.applicationRepository = applicationRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    // ==================== GET ====================

    @GetMapping
    @PreAuthorize("hasAuthority('application.view')")
    public List<ApplicationResponseDto> list(@RequestParam(required = false) String status) {
        return service.getAll().stream()
                .filter(a -> status == null || a.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('application.view')")
    public Page<ApplicationResponseDto> filterApplications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Pageable pageableWithoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted());
        Application.Status statusEnum = status != null ? Application.Status.valueOf(status.toUpperCase()) : null;
        return service.getFilteredApplications(statusEnum, programId, searchQuery, startDate, endDate, pageableWithoutSort);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('application.view')")
    public ApplicationResponseDto getOne(@PathVariable Long id) {
        return service.getOne(id);
    }

    // ==================== CREATE ====================

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> create(@Valid @RequestBody ApplicationCreateDto dto) {
        try {
            ApplicationResponseDto result = service.create(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Ошибка при создании заявки: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }

    @PostMapping(value = "/create-with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> createWithFiles(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            System.out.println("=== ПОЛУЧЕН ЗАПРОС НА СОЗДАНИЕ ЗАЯВКИ С ФАЙЛАМИ ===");
            System.out.println("JSON data: " + dataJson);
            System.out.println("Количество файлов: " + (files != null ? files.size() : 0));
            if (files != null && !files.isEmpty()) {
                for (int i = 0; i < files.size(); i++) {
                    System.out.println("Файл " + (i+1) + ": " + files.get(i).getOriginalFilename() +
                            ", размер: " + files.get(i).getSize() + ", тип: " + files.get(i).getContentType());
                }
            }
            ApplicationCreateDto dto = objectMapper.readValue(dataJson, ApplicationCreateDto.class);
            System.out.println("Распарсенный DTO получен");

            Set<ConstraintViolation<ApplicationCreateDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<ApplicationCreateDto> violation : violations) {
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
                System.out.println("Ошибки валидации: " + errors);
                return ResponseEntity.badRequest().body(Map.of("error", "Validation failed", "details", errors, "status", 400));
            }

            ApplicationResponseDto application = service.create(dto);
            System.out.println("Заявка создана с ID: " + application.getId());

            if (files != null && !files.isEmpty()) {
                System.out.println("Начинаем загрузку " + files.size() + " файлов...");
                for (MultipartFile file : files) {
                    try {
                        System.out.println("Загрузка файла: " + file.getOriginalFilename());
                        var uploadRes = storageService.upload(file);
                        String key = uploadRes.key();
                        String fileUrl = uploadRes.url();
                        FileCreateDto fileDto = new FileCreateDto(
                                "APPLICATION",
                                application.getId(),
                                key,
                                file.getOriginalFilename(),
                                fileUrl,
                                file.getContentType(),
                                file.getSize(),
                                null
                        );
                        filesService.create(fileDto);
                        System.out.println("Файл успешно загружен: " + fileUrl);
                    } catch (Exception e) {
                        System.err.println("ОШИБКА загрузки файла " + file.getOriginalFilename() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                System.out.println("Загрузка файлов завершена");
            }
            System.out.println("=== ЗАЯВКА УСПЕШНО СОЗДАНА ===");
            return ResponseEntity.ok(application);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            System.err.println("ОШИБКА ПАРСИНГА JSON: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid JSON format");
            errorResponse.put("message", "Неверный формат JSON данных: " + e.getMessage());
            errorResponse.put("timestamp", OffsetDateTime.now());
            errorResponse.put("status", 400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (DataIntegrityViolationException e) {
            System.err.println("ОШИБКА ЦЕЛОСТНОСТИ ДАННЫХ: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Data Integrity Error");
            errorResponse.put("message", "Ошибка целостности данных: " + e.getMessage());
            errorResponse.put("timestamp", OffsetDateTime.now());
            errorResponse.put("status", 409);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (Exception e) {
            System.err.println("!!! КРИТИЧЕСКАЯ ОШИБКА В createWithFiles !!!");
            System.err.println("Тип ошибки: " + e.getClass().getName());
            System.err.println("Сообщение: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("exception", e.getClass().getName());
            errorResponse.put("cause", e.getCause() != null ? e.getCause().getMessage() : null);
            errorResponse.put("timestamp", OffsetDateTime.now());
            errorResponse.put("status", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('application.update')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ApplicationCreateDto dto) {
        try {
            ApplicationResponseDto result = service.update(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении заявки: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }

    // ==================== УПРАВЛЕНИЕ ЗАЯВКОЙ ====================
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('application.accept', 'application.reject')")
    public ApplicationResponseDto updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String rejectionReason
    ) {
        return service.updateStatus(id, status, rejectionReason);
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasAuthority('application.enroll')")
    public ResponseEntity<?> enrollStudent(@PathVariable Long id,
                                           @RequestParam(defaultValue = "1") int academicYear) {
        try {
            Application app = applicationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
            if (app.getStatus() != Application.Status.ACCEPTED) {
                return ResponseEntity.badRequest().body(Map.of("error", "Заявление должно быть одобрено перед зачислением"));
            }
            enrollmentService.enrollFromApplication(app, academicYear);
            return ResponseEntity.ok(Map.of("message", "Студент успешно зачислен"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка зачисления: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/consents")
    @PreAuthorize("hasAuthority('application.update')")
    public ApplicationResponseDto updateConsents(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean consentPersonalData,
            @RequestParam(required = false) Boolean consentPhotoVideo,
            @RequestParam(required = false) Boolean consentMedicalIntervention
    ) {
        return service.updateConsents(id, consentPersonalData, consentPhotoVideo, consentMedicalIntervention);
    }

    @PatchMapping("/{id}/comment")
    @PreAuthorize("hasAuthority('application.update')")
    public ApplicationResponseDto updateOfficerComment(
            @PathVariable Long id,
            @RequestParam String officerComment
    ) {
        return service.updateOfficerComment(id, officerComment);
    }

    @PatchMapping("/{id}/internal-notes")
    @PreAuthorize("hasAuthority('application.update')")
    public ApplicationResponseDto updateInternalNotes(
            @PathVariable Long id,
            @RequestParam String internalNotes
    ) {
        return service.updateInternalNotes(id, internalNotes);
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('application.assign_exam')")
    public ApplicationResponseDto assignOfficer(@PathVariable Long id, @RequestParam Long officerId) {
        return service.assignOfficer(id, officerId);
    }

    @PatchMapping("/{id}/enrollment-date")
    @PreAuthorize("hasAuthority('application.update')")
    public ApplicationResponseDto updateEnrollmentDate(
            @PathVariable Long id,
            @RequestParam LocalDate enrollmentDate
    ) {
        return service.updateEnrollmentDate(id, enrollmentDate);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('application.delete')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ==================== STATISTICS ====================

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('application.view')")
    public Map<String, Long> getStatistics() {
        return service.getStatistics();
    }
}