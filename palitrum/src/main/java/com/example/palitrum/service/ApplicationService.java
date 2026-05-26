package com.example.palitrum.service;

import com.example.palitrum.dto.ApplicationCreateDto;
import com.example.palitrum.dto.ApplicationResponseDto;
import com.example.palitrum.dto.UserRelationDTO;
import com.example.palitrum.dto.UserRoleDTO;
import com.example.palitrum.model.Application;
import com.example.palitrum.model.FileEntityType;
import com.example.palitrum.model.Program;
import com.example.palitrum.model.User;
import com.example.palitrum.repository.ApplicationRepository;
import com.example.palitrum.repository.ProgramRepository;
import com.example.palitrum.repository.UserRepository;
import com.example.palitrum.util.TranslitUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ApplicationRepository repository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FilesService filesService;
    private final FileStorageService storageService;

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final UserRelationService userRelationService;
    private final EnrollmentService enrollmentService;   // 🔹 добавлено поле для зачисления

    // ========== ОСНОВНЫЕ МЕТОДЫ ==========
    public List<ApplicationResponseDto> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ApplicationResponseDto getOne(Long id) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        return toResponse(app);
    }

    @Transactional
    public ApplicationResponseDto create(ApplicationCreateDto dto) {
        Application app = new Application();

        // ---------- Ребёнок ----------
        app.setChildLastName(dto.childLastName());
        app.setChildFirstName(dto.childFirstName());
        app.setChildMiddleName(dto.childMiddleName());
        app.setChildBirthDate(dto.childBirthDate());
        app.setChildBirthPlace(dto.childBirthPlace());
        app.setChildCitizenship(dto.childCitizenship());
        app.setChildAddress(dto.childAddress());
        app.setChildSnils(dto.childSnils());
        app.setChildIndividualPlan(Optional.ofNullable(dto.childIndividualPlan()).orElse(false));
        app.setChildLastSchool(dto.childLastSchool());
        app.setChildGradeLevel(dto.childGradeLevel());

        // ---------- Родитель ----------
        app.setParentLastName(dto.parentLastName());
        app.setParentFirstName(dto.parentFirstName());
        app.setParentMiddleName(dto.parentMiddleName());
        app.setParentRelation(dto.parentRelation());
        app.setParentPhone(dto.parentPhone());
        app.setParentEmail(dto.parentEmail());

        // ---------- Программы ----------
        if (dto.preferredProgramId() != null) {
            Program program = programRepository.findById(dto.preferredProgramId())
                    .orElseThrow(() -> new IllegalArgumentException("Программа не найдена"));
            app.setPreferredProgram(program);
        }
        if (dto.finalProgramId() != null) {
            Program finalProgram = programRepository.findById(dto.finalProgramId())
                    .orElseThrow(() -> new IllegalArgumentException("Итоговая программа не найдена"));
            app.setFinalProgram(finalProgram);
        }

        // ---------- Согласия ----------
        app.setConsentPersonalData(Optional.ofNullable(dto.consentPersonalData()).orElse(false));
        app.setConsentPhotoVideo(Optional.ofNullable(dto.consentPhotoVideo()).orElse(false));
        app.setConsentMedicalIntervention(Optional.ofNullable(dto.consentMedicalIntervention()).orElse(false));

        // ---------- Дополнительная информация ----------
        app.setAdditionalInfo(dto.additionalInfo());
        app.setSource(dto.source() != null ? dto.source() : Application.Source.SITE);

        // ---------- Временная метка подачи ----------
        app.setSubmittedAt(OffsetDateTime.now());

        Application saved = repository.save(app);
        return toResponse(saved);
    }

    @Transactional
    public ApplicationResponseDto update(Long id, ApplicationCreateDto dto) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));

        // Ребёнок
        app.setChildFirstName(dto.childFirstName());
        app.setChildLastName(dto.childLastName());
        app.setChildMiddleName(dto.childMiddleName());
        app.setChildBirthDate(dto.childBirthDate());
        app.setChildBirthPlace(dto.childBirthPlace());
        app.setChildCitizenship(dto.childCitizenship());
        app.setChildAddress(dto.childAddress());
        app.setChildSnils(dto.childSnils());
        app.setChildIndividualPlan(Optional.ofNullable(dto.childIndividualPlan()).orElse(false));
        app.setChildLastSchool(dto.childLastSchool());
        app.setChildGradeLevel(dto.childGradeLevel());

        // Родитель
        app.setParentFirstName(dto.parentFirstName());
        app.setParentLastName(dto.parentLastName());
        app.setParentMiddleName(dto.parentMiddleName());
        app.setParentPhone(dto.parentPhone());
        app.setParentEmail(dto.parentEmail());
        app.setParentRelation(dto.parentRelation());

        // Программы
        if (dto.preferredProgramId() != null) {
            Program program = programRepository.findById(dto.preferredProgramId())
                    .orElseThrow(() -> new IllegalArgumentException("Программа не найдена"));
            app.setPreferredProgram(program);
        } else {
            app.setPreferredProgram(null);
        }
        if (dto.finalProgramId() != null) {
            Program finalProgram = programRepository.findById(dto.finalProgramId())
                    .orElseThrow(() -> new IllegalArgumentException("Итоговая программа не найдена"));
            app.setFinalProgram(finalProgram);
        } else {
            app.setFinalProgram(null);
        }

        // Согласия
        app.setConsentPersonalData(Optional.ofNullable(dto.consentPersonalData()).orElse(false));
        app.setConsentPhotoVideo(Optional.ofNullable(dto.consentPhotoVideo()).orElse(false));
        app.setConsentMedicalIntervention(Optional.ofNullable(dto.consentMedicalIntervention()).orElse(false));

        // Дополнительно
        app.setAdditionalInfo(dto.additionalInfo());
        app.setSource(dto.source() != null ? dto.source() : app.getSource());

        Application saved = repository.save(app);
        ApplicationResponseDto responseDto = toResponse(saved);
        messagingTemplate.convertAndSend("/topic/applications", responseDto);
        return responseDto;
    }

    @Transactional
    public ApplicationResponseDto updateStatus(Long id, String status, String rejectionReason) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));

        Application.Status newStatus = Application.Status.valueOf(status);
        Application.Status oldStatus = app.getStatus();

        app.setStatus(newStatus);

        // Установка временных меток и причин
        if (newStatus == Application.Status.REVIEWED && app.getReviewedAt() == null) {
            app.setReviewedAt(OffsetDateTime.now());
        }
        if (newStatus == Application.Status.ACCEPTED || newStatus == Application.Status.REJECTED) {
            app.setDecisionAt(OffsetDateTime.now());
        }
        if (newStatus == Application.Status.REJECTED && rejectionReason != null) {
            app.setRejectionReason(rejectionReason);
        }

        if (newStatus == Application.Status.ACCEPTED && oldStatus != Application.Status.ACCEPTED) {
            // 1. Создаём пользователей (ребёнок, родитель)
            createUsersFromApplication(app);

            // 2. Зачисляем студента на программу (год обучения 1)
            try {
                enrollmentService.enrollFromApplication(app, 1);
                log.info("Студент успешно зачислен на программу из заявки {}", id);
            } catch (Exception e) {
                log.error("Ошибка при зачислении студента из заявки {}: {}", id, e.getMessage(), e);
                // Не прерываем выполнение, статус заявки всё равно ACCEPTED
            }
        }

        Application saved = repository.save(app);
        ApplicationResponseDto dto = toResponse(saved);
        messagingTemplate.convertAndSend("/topic/applications", dto);
        return dto;
    }

    @Transactional
    public ApplicationResponseDto updateConsents(Long id, Boolean consentPersonalData,
                                                 Boolean consentPhotoVideo, Boolean consentMedicalIntervention) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        if (consentPersonalData != null) app.setConsentPersonalData(consentPersonalData);
        if (consentPhotoVideo != null) app.setConsentPhotoVideo(consentPhotoVideo);
        if (consentMedicalIntervention != null) app.setConsentMedicalIntervention(consentMedicalIntervention);
        Application saved = repository.save(app);
        return toResponse(saved);
    }

    @Transactional
    public ApplicationResponseDto updateOfficerComment(Long id, String officerComment) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        app.setOfficerComment(officerComment);
        Application saved = repository.save(app);
        return toResponse(saved);
    }

    @Transactional
    public ApplicationResponseDto updateInternalNotes(Long id, String internalNotes) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        app.setInternalNotes(internalNotes);
        Application saved = repository.save(app);
        return toResponse(saved);
    }

    @Transactional
    public ApplicationResponseDto updateEnrollmentDate(Long id, LocalDate enrollmentDate) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        app.setEnrollmentDate(enrollmentDate);
        Application saved = repository.save(app);
        return toResponse(saved);
    }

    @Transactional
    public ApplicationResponseDto assignOfficer(Long id, Long officerId) {
        Application app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        User user = userRepository.findById(officerId)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));
        app.setAssignedOfficer(user);
        Application saved = repository.save(app);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        var files = filesService.listByEntity(FileEntityType.APPLICATION, id);
        for (var file : files) {
            try {
                if (file.storageKey() != null && !file.storageKey().isBlank()) {
                    storageService.delete(file.storageKey());
                }
            } catch (Exception e) {
                System.err.println("Ошибка при удалении файла из хранилища: " + e.getMessage());
            }
        }
        for (var file : files) {
            try {
                filesService.delete(file.id());
            } catch (Exception e) {
                System.err.println("Ошибка при удалении записи файла: " + e.getMessage());
            }
        }
        repository.deleteById(id);
    }

    public Page<ApplicationResponseDto> getFilteredApplications(
            Application.Status status,
            Long programId,
            String searchQuery,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            Pageable pageable
    ) {
        Page<Application> page = repository.filterApplications(
                status != null ? status.name() : null,
                programId,
                searchQuery,
                startDate,
                endDate,
                pageable
        );
        return page.map(this::toResponse);
    }

    public Page<ApplicationResponseDto> getByStatus(Application.Status status, Pageable pageable) {
        return repository.findByStatus(status, pageable).map(this::toResponse);
    }

    public Page<ApplicationResponseDto> getByStatusAndChildName(
            Application.Status status,
            String lastName,
            String firstName,
            Pageable pageable
    ) {
        return repository.findByStatusAndChildName(status, lastName, firstName, pageable)
                .map(this::toResponse);
    }

    public Page<ApplicationResponseDto> getByCreatedDateRange(
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            Pageable pageable
    ) {
        return repository.findByCreatedAtBetween(startDate, endDate, pageable)
                .map(this::toResponse);
    }

    public Page<ApplicationResponseDto> getByPreferredProgram(Long programId, Pageable pageable) {
        return repository.findByPreferredProgramId(programId, pageable)
                .map(this::toResponse);
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", repository.count());
        stats.put("newToday", repository.countCreatedToday());
        stats.put("newAll", repository.countByStatus(Application.Status.NEW));
        stats.put("waitingDocs", repository.countWaitingDocs());
        stats.put("enrolled", repository.countEnrolled());
        OffsetDateTime twentyFourHoursAgo = OffsetDateTime.now(ZoneOffset.UTC).minusHours(24);
        stats.put("urgent", repository.countOverdueNew(twentyFourHoursAgo));
        stats.put("awaitingInfo", repository.countByStatus(Application.Status.WAITLIST));
        return stats;
    }

    // ---------- Приватные вспомогательные методы ----------
    private void createUsersFromApplication(Application app) {
        try {
            String childPassword = generatePassword(app.getChildLastName(), app.getChildBirthDate());
            User child = userService.createChildFromApplication(app, childPassword);
            app.setChildUserId(child.getId());

            String parentPassword = generatePassword(app.getParentLastName(), app.getChildBirthDate());
            User parent = userService.createParentFromApplication(app, parentPassword);
            app.setParentUserId(parent.getId());

            assignRole(child.getId(), "STUDENT");
            assignRole(parent.getId(), "PARENT");

            String relationType = mapRelationType(app.getParentRelation());
            UserRelationDTO relationDTO = new UserRelationDTO();
            relationDTO.setParentUserId(parent.getId());
            relationDTO.setChildUserId(child.getId());
            relationDTO.setRelationType(relationType);
            relationDTO.setVerified(true);
            userRelationService.create(relationDTO);

            repository.save(app);

            log.info("Для заявки {} созданы пользователи: ребёнок id={}, родитель id={}", app.getId(), child.getId(), parent.getId());
            log.debug("Пароль ребёнка {}: {}", child.getEmail(), childPassword);
            log.debug("Пароль родителя {}: {}", parent.getEmail(), parentPassword);
        } catch (Exception e) {
            log.error("Ошибка при создании пользователей из заявки {}: {}", app.getId(), e.getMessage(), e);
            throw new RuntimeException("Не удалось создать учётные записи для одобренной заявки", e);
        }
    }

    private String generatePassword(String lastName, LocalDate birthDate) {
        String latinLastName = TranslitUtils.toLatin(lastName);
        String birthStr = birthDate.format(DATE_FORMATTER);
        return latinLastName + "_" + birthStr;
    }

    private void assignRole(Long userId, String roleName) {
        var role = userRoleService.getRoleByName(roleName);
        if (role == null) {
            throw new RuntimeException("Роль " + roleName + " не найдена в БД");
        }
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUserId(userId);
        dto.setRoleId(role.getId());
        dto.setScopeType(null);
        dto.setScopeId(null);
        dto.setAssignedBy(null);
        userRoleService.create(dto);
    }

    private String mapRelationType(Application.ParentRelation relation) {
        switch (relation) {
            case MOTHER: return "parent";
            case FATHER: return "parent";
            case GUARDIAN: return "guardian";
            default: return "parent";
        }
    }

    private ApplicationResponseDto toResponse(Application app) {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(app.getId());

        // Ребёнок
        dto.setChildLastName(app.getChildLastName());
        dto.setChildFirstName(app.getChildFirstName());
        dto.setChildMiddleName(app.getChildMiddleName());
        dto.setChildBirthDate(app.getChildBirthDate());
        dto.setChildBirthPlace(app.getChildBirthPlace());
        dto.setChildCitizenship(app.getChildCitizenship());
        dto.setChildAddress(app.getChildAddress());
        dto.setChildSnils(app.getChildSnils());
        dto.setChildIndividualPlan(app.getChildIndividualPlan());
        dto.setChildLastSchool(app.getChildLastSchool());
        dto.setChildGradeLevel(app.getChildGradeLevel());

        // Родитель
        dto.setParentLastName(app.getParentLastName());
        dto.setParentFirstName(app.getParentFirstName());
        dto.setParentMiddleName(app.getParentMiddleName());
        dto.setParentRelation(app.getParentRelation().name());
        dto.setParentPhone(app.getParentPhone());
        dto.setParentEmail(app.getParentEmail());

        // Программы
        dto.setPreferredProgramId(app.getPreferredProgram() != null ? app.getPreferredProgram().getId() : null);
        dto.setPreferredProgramName(app.getPreferredProgram() != null ? app.getPreferredProgram().getName() : null);
        dto.setFinalProgramId(app.getFinalProgram() != null ? app.getFinalProgram().getId() : null);
        dto.setFinalProgramName(app.getFinalProgram() != null ? app.getFinalProgram().getName() : null);

        // Согласия
        dto.setConsentPersonalData(app.getConsentPersonalData());
        dto.setConsentPhotoVideo(app.getConsentPhotoVideo());
        dto.setConsentMedicalIntervention(app.getConsentMedicalIntervention());

        // Доп. информация
        dto.setAdditionalInfo(app.getAdditionalInfo());

        // Статус и обработка
        dto.setStatus(app.getStatus().getValue());
        dto.setRejectionReason(app.getRejectionReason());
        dto.setAssignedOfficerId(app.getAssignedOfficer() != null ? app.getAssignedOfficer().getId() : null);
        dto.setSource(app.getSource().getValue());

        // Временные метки процесса
        dto.setSubmittedAt(app.getSubmittedAt());
        dto.setReviewedAt(app.getReviewedAt());
        dto.setDecisionAt(app.getDecisionAt());
        dto.setEnrollmentDate(app.getEnrollmentDate());
        dto.setWaitlistExpiryDate(app.getWaitlistExpiryDate());

        // Комментарии и история
        dto.setInternalNotes(app.getInternalNotes());
        dto.setOfficerComment(app.getOfficerComment());
        dto.setHistoryChanges(app.getHistoryChanges());

        // Связь с пользователями
        dto.setChildUserId(app.getChildUserId());
        dto.setParentUserId(app.getParentUserId());

        // Системные поля
        dto.setCreatedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());

        return dto;
    }
}