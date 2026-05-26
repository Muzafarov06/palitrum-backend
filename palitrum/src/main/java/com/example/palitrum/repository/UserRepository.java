package com.example.palitrum.repository;

import com.example.palitrum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles ur " +
            "LEFT JOIN FETCH ur.role r " +
            "LEFT JOIN FETCH r.rolePermissions rp " +
            "LEFT JOIN FETCH rp.permission " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone, Long id);

    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles ur WHERE ur.role.name = 'TEACHER'")
    List<User> findTeachers();

    @Query(value = "SELECT * FROM users u WHERE " +
            "(CAST(:status AS VARCHAR) IS NULL OR u.status = CAST(:status AS VARCHAR)) AND " +
            "(CAST(:roleName AS VARCHAR) IS NULL OR EXISTS (SELECT 1 FROM user_role ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id = u.id AND r.name = CAST(:roleName AS VARCHAR))) AND " +
            "(CAST(:search AS VARCHAR) IS NULL OR (LOWER(u.first_name) LIKE LOWER(CONCAT('%', CAST(:search AS VARCHAR), '%')) OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', CAST(:search AS VARCHAR), '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS VARCHAR), '%')) OR u.phone LIKE CONCAT('%', CAST(:search AS VARCHAR), '%'))) AND " +
            "(CAST(:birthDateFrom AS DATE) IS NULL OR u.birth_date >= CAST(:birthDateFrom AS DATE)) AND " +
            "(CAST(:birthDateTo AS DATE) IS NULL OR u.birth_date <= CAST(:birthDateTo AS DATE))",
            countQuery = "SELECT COUNT(*) FROM users u WHERE " +
                    "(CAST(:status AS VARCHAR) IS NULL OR u.status = CAST(:status AS VARCHAR)) AND " +
                    "(CAST(:roleName AS VARCHAR) IS NULL OR EXISTS (SELECT 1 FROM user_role ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id = u.id AND r.name = CAST(:roleName AS VARCHAR))) AND " +
                    "(CAST(:search AS VARCHAR) IS NULL OR (LOWER(u.first_name) LIKE LOWER(CONCAT('%', CAST(:search AS VARCHAR), '%')) OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', CAST(:search AS VARCHAR), '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS VARCHAR), '%')) OR u.phone LIKE CONCAT('%', CAST(:search AS VARCHAR), '%'))) AND " +
                    "(CAST(:birthDateFrom AS DATE) IS NULL OR u.birth_date >= CAST(:birthDateFrom AS DATE)) AND " +
                    "(CAST(:birthDateTo AS DATE) IS NULL OR u.birth_date <= CAST(:birthDateTo AS DATE))",
            nativeQuery = true)
    Page<User> searchUsers(@Param("status") String status,
                           @Param("roleName") String roleName,
                           @Param("search") String search,
                           @Param("birthDateFrom") java.time.LocalDate birthDateFrom,
                           @Param("birthDateTo") java.time.LocalDate birthDateTo,
                           Pageable pageable);

    long countByStatus(String status);
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles ur WHERE u.status = :status AND ur.role.name = :roleName")
    long countByStatusAndRole(@Param("status") String status, @Param("roleName") String roleName);
}