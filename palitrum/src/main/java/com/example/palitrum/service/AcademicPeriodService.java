package com.example.palitrum.service;

import com.example.palitrum.dto.AcademicPeriodDto;
import com.example.palitrum.dto.AcademicPeriodResponse;
import com.example.palitrum.model.AcademicPeriod;
import com.example.palitrum.repository.AcademicPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AcademicPeriodService {

    private final AcademicPeriodRepository repository;

    @Transactional(readOnly = true)
    public Page<AcademicPeriodResponse> getAll(String search, Pageable pageable) {
        Page<AcademicPeriod> page;
        if (search != null && !search.isBlank()) {
            page = repository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AcademicPeriodResponse getById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Период не найден")));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", repository.count());
        stats.put("SEMESTER", repository.countByPeriodType(AcademicPeriod.PeriodType.SEMESTER));
        stats.put("QUARTER", repository.countByPeriodType(AcademicPeriod.PeriodType.QUARTER));
        stats.put("YEAR", repository.countByPeriodType(AcademicPeriod.PeriodType.YEAR));
        stats.put("current", repository.countByIsCurrentTrue());
        return stats;
    }

    @Transactional
    public AcademicPeriodResponse create(AcademicPeriodDto dto) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }
        AcademicPeriod period = AcademicPeriod.builder()
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .periodType(AcademicPeriod.PeriodType.valueOf(dto.getPeriodType()))
                .isCurrent(dto.getIsCurrent() != null && dto.getIsCurrent())
                .build();

        if (Boolean.TRUE.equals(period.getIsCurrent())) {
            repository.resetCurrentFlag();
        }
        return toResponse(repository.save(period));
    }

    @Transactional
    public AcademicPeriodResponse update(Long id, AcademicPeriodDto dto) {
        AcademicPeriod period = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Период не найден"));

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }

        period.setName(dto.getName());
        period.setStartDate(dto.getStartDate());
        period.setEndDate(dto.getEndDate());
        period.setPeriodType(AcademicPeriod.PeriodType.valueOf(dto.getPeriodType()));

        boolean newCurrent = dto.getIsCurrent() != null && dto.getIsCurrent();
        if (newCurrent && !Boolean.TRUE.equals(period.getIsCurrent())) {
            repository.resetCurrentFlag();
        }
        period.setIsCurrent(newCurrent);

        return toResponse(repository.save(period));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private AcademicPeriodResponse toResponse(AcademicPeriod period) {
        return new AcademicPeriodResponse(
                period.getId(),
                period.getName(),
                period.getStartDate(),
                period.getEndDate(),
                period.getPeriodType().name(),
                period.getIsCurrent(),
                period.getCreatedAt(),
                period.getUpdatedAt()
        );
    }
}