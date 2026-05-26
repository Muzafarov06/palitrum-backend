package com.example.palitrum.repository;

import com.example.palitrum.model.AcademicPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {

    Optional<AcademicPeriod> findByIsCurrentTrue();

    Optional<AcademicPeriod> findFirstByIsCurrentTrue();

    Optional<AcademicPeriod> findByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE AcademicPeriod SET isCurrent = false WHERE isCurrent = true")
    void resetCurrentFlag();

    Page<AcademicPeriod> findByNameContainingIgnoreCase(String name, Pageable pageable);

    long countByPeriodType(AcademicPeriod.PeriodType periodType);
    long countByIsCurrentTrue();
}