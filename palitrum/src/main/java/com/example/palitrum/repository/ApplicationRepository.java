package com.example.palitrum.repository;

import com.example.palitrum.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // ========== БАЗОВЫЕ МЕТОДЫ ==========
    Page<Application> findByStatus(Application.Status status, Pageable pageable);
    long countByStatus(Application.Status status);

    // ========== ПОИСК ПО СВЯЗАННЫМ ПОЛЬЗОВАТЕЛЯМ ==========
    @Query("SELECT a FROM Application a WHERE a.childUserId = :userId OR a.parentUserId = :userId")
    Optional<Application> findByUserId(@Param("userId") Long userId);

    // ========== ПОИСК ПО СНИЛС, EMAIL, КЛАССУ ==========
    Optional<Application> findByChildSnils(String childSnils);
    List<Application> findByChildSnilsContainingIgnoreCase(String snilsPart);
    Page<Application> findByParentEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Application> findByChildGradeLevel(String gradeLevel, Pageable pageable);

    // ========== ФИЛЬТРАЦИЯ ПО ДАТАМ ==========
    Page<Application> findByEnrollmentDateBetween(LocalDate from, LocalDate to, Pageable pageable);
    Page<Application> findByDecisionAtBetween(OffsetDateTime from, OffsetDateTime to, Pageable pageable);
    Page<Application> findByCreatedAtBetween(OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);
    Page<Application> findByPreferredProgramId(Long programId, Pageable pageable);

    // ========== СТАТИСТИКА ПО СТАТУСАМ ==========
    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'WAITING_DOCS'")
    long countWaitingDocs();
    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'ACCEPTED' AND a.enrollmentDate IS NOT NULL")
    long countEnrolled();

    // ========== ОСНОВНОЙ НАТИВНЫЙ МЕТОД ФИЛЬТРАЦИИ С РАСШИРЕННЫМ ПОИСКОМ ==========
    @Query(value = "SELECT a.* FROM applications a " +
            "WHERE (CAST(:status AS VARCHAR) IS NULL OR a.status = CAST(:status AS VARCHAR)) " +
            "AND (CAST(:programId AS VARCHAR) IS NULL OR a.preferred_program_id = CAST(:programId AS BIGINT)) " +
            "AND (CAST(:searchQuery AS VARCHAR) IS NULL OR " +
            "     LOWER(a.child_last_name) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS VARCHAR), '%')) OR " +
            "     LOWER(a.child_first_name) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS VARCHAR), '%')) OR " +
            "     LOWER(a.child_snils) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS VARCHAR), '%')) OR " +
            "     LOWER(a.parent_email) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS VARCHAR), '%'))) " +
            "AND (CAST(:startDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR a.created_at >= CAST(:startDate AS TIMESTAMP WITH TIME ZONE)) " +
            "AND (CAST(:endDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR a.created_at <= CAST(:endDate AS TIMESTAMP WITH TIME ZONE)) " +
            "ORDER BY a.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM applications a " +
                    "WHERE (CAST(:status AS VARCHAR) IS NULL OR a.status = CAST(:status AS VARCHAR)) " +
                    "AND (CAST(:programId AS VARCHAR) IS NULL OR a.preferred_program_id = CAST(:programId AS BIGINT)) " +
                    "AND (CAST(:searchQuery AS VARCHAR) IS NULL OR " +
                    "     LOWER(a.child_last_name) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS VARCHAR), '%')) OR " +
                    "     LOWER(a.child_first_name) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS VARCHAR), '%')) OR " +
                    "     LOWER(a.child_snils) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS VARCHAR), '%')) OR " +
                    "     LOWER(a.parent_email) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS VARCHAR), '%'))) " +
                    "AND (CAST(:startDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR a.created_at >= CAST(:startDate AS TIMESTAMP WITH TIME ZONE)) " +
                    "AND (CAST(:endDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR a.created_at <= CAST(:endDate AS TIMESTAMP WITH TIME ZONE))",
            nativeQuery = true)
    Page<Application> filterApplications(@Param("status") String status,
                                         @Param("programId") Long programId,
                                         @Param("searchQuery") String searchQuery,
                                         @Param("startDate") OffsetDateTime startDate,
                                         @Param("endDate") OffsetDateTime endDate,
                                         Pageable pageable);

    // ========== МЕТОДЫ ДЛЯ ФИЛЬТРАЦИИ ПО СТАТУСУ + ИМЕНИ ==========
    @Query(value = "SELECT a.* FROM applications a WHERE " +
            "(CAST(:status AS VARCHAR) IS NULL OR a.status = CAST(:status AS VARCHAR)) " +
            "AND (CAST(:lastName AS VARCHAR) IS NULL OR LOWER(a.child_last_name) LIKE LOWER(CONCAT('%', CAST(:lastName AS VARCHAR), '%'))) " +
            "AND (CAST(:firstName AS VARCHAR) IS NULL OR LOWER(a.child_first_name) LIKE LOWER(CONCAT('%', CAST(:firstName AS VARCHAR), '%'))) " +
            "ORDER BY a.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM applications a WHERE " +
                    "(CAST(:status AS VARCHAR) IS NULL OR a.status = CAST(:status AS VARCHAR)) " +
                    "AND (CAST(:lastName AS VARCHAR) IS NULL OR LOWER(a.child_last_name) LIKE LOWER(CONCAT('%', CAST(:lastName AS VARCHAR), '%'))) " +
                    "AND (CAST(:firstName AS VARCHAR) IS NULL OR LOWER(a.child_first_name) LIKE LOWER(CONCAT('%', CAST(:firstName AS VARCHAR), '%')))",
            nativeQuery = true)
    Page<Application> findByStatusAndChildName(@Param("status") Application.Status status,
                                               @Param("lastName") String lastName,
                                               @Param("firstName") String firstName,
                                               Pageable pageable);

    // ========== СТАРЫЙ МЕТОД ДЛЯ СОВМЕСТИМОСТИ (оставлен, но рекомендуется использовать filterApplications) ==========
    @Query(value = "SELECT a.* FROM applications a " +
            "LEFT JOIN programs p ON a.preferred_program_id = p.id " +
            "WHERE (CAST(:status AS VARCHAR) IS NULL OR a.status = CAST(:status AS VARCHAR)) " +
            "AND (CAST(:programId AS VARCHAR) IS NULL OR a.preferred_program_id = CAST(:programId AS BIGINT)) " +
            "AND (CAST(:lastName AS VARCHAR) IS NULL OR LOWER(a.child_last_name) LIKE LOWER(CONCAT('%', CAST(:lastName AS VARCHAR), '%'))) " +
            "AND (CAST(:firstName AS VARCHAR) IS NULL OR LOWER(a.child_first_name) LIKE LOWER(CONCAT('%', CAST(:firstName AS VARCHAR), '%'))) " +
            "AND (CAST(:startDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR a.created_at >= CAST(:startDate AS TIMESTAMP WITH TIME ZONE)) " +
            "AND (CAST(:endDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR a.created_at <= CAST(:endDate AS TIMESTAMP WITH TIME ZONE)) " +
            "ORDER BY a.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM applications a " +
                    "LEFT JOIN programs p ON a.preferred_program_id = p.id " +
                    "WHERE (CAST(:status AS VARCHAR) IS NULL OR a.status = CAST(:status AS VARCHAR)) " +
                    "AND (CAST(:programId AS VARCHAR) IS NULL OR a.preferred_program_id = CAST(:programId AS BIGINT)) " +
                    "AND (CAST(:lastName AS VARCHAR) IS NULL OR LOWER(a.child_last_name) LIKE LOWER(CONCAT('%', CAST(:lastName AS VARCHAR), '%'))) " +
                    "AND (CAST(:firstName AS VARCHAR) IS NULL OR LOWER(a.child_first_name) LIKE LOWER(CONCAT('%', CAST(:firstName AS VARCHAR), '%'))) " +
                    "AND (CAST(:startDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR a.created_at >= CAST(:startDate AS TIMESTAMP WITH TIME ZONE)) " +
                    "AND (CAST(:endDate AS TIMESTAMP WITH TIME ZONE) IS NULL OR a.created_at <= CAST(:endDate AS TIMESTAMP WITH TIME ZONE))",
            nativeQuery = true)
    Page<Application> searchApplications(@Param("status") Application.Status status,
                                         @Param("programId") Long programId,
                                         @Param("lastName") String lastName,
                                         @Param("firstName") String firstName,
                                         @Param("startDate") OffsetDateTime startDate,
                                         @Param("endDate") OffsetDateTime endDate,
                                         Pageable pageable);

    // ========== МЕТОДЫ ДЛЯ СТАТИСТИКИ ==========
    long count();
    @Query("SELECT COUNT(a) FROM Application a WHERE FUNCTION('DATE', a.createdAt) = CURRENT_DATE")
    long countCreatedToday();
    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'NEW' AND a.createdAt < :cutoff")
    long countOverdueNew(@Param("cutoff") OffsetDateTime cutoff);
}