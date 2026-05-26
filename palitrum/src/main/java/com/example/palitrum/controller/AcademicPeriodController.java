package com.example.palitrum.controller;

import com.example.palitrum.dto.AcademicPeriodDto;
import com.example.palitrum.dto.AcademicPeriodResponse;
import com.example.palitrum.service.AcademicPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/academic-periods")
@RequiredArgsConstructor
public class AcademicPeriodController {

    private final AcademicPeriodService service;

    @GetMapping
    @PreAuthorize("hasAuthority('academic_period.view')")
    public ResponseEntity<Page<AcademicPeriodResponse>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable) {
        return ResponseEntity.ok(service.getAll(search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('academic_period.view')")
    public ResponseEntity<AcademicPeriodResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('academic_period.view')")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        return ResponseEntity.ok(service.getStatistics());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('academic_period.create')")
    public ResponseEntity<AcademicPeriodResponse> create(@Valid @RequestBody AcademicPeriodDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('academic_period.update')")
    public ResponseEntity<AcademicPeriodResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody AcademicPeriodDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('academic_period.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}