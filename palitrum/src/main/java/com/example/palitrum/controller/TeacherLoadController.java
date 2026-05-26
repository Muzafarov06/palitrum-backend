package com.example.palitrum.controller;

import com.example.palitrum.dto.TeacherLoadDto;
import com.example.palitrum.dto.TeacherLoadResponse;
import com.example.palitrum.service.TeacherLoadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher-loads")
@RequiredArgsConstructor
public class TeacherLoadController {

    private final TeacherLoadService service;

    @GetMapping
    @PreAuthorize("hasAuthority('teacher_load.view')")
    public ResponseEntity<List<TeacherLoadResponse>> getAll(
            @RequestParam(required = false) Long periodId) {
        return ResponseEntity.ok(service.getAll(periodId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('teacher_load.create')")
    public ResponseEntity<TeacherLoadResponse> create(@Valid @RequestBody TeacherLoadDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('teacher_load.update')")
    public ResponseEntity<TeacherLoadResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody TeacherLoadDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('teacher_load.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recalculate/{id}")
    @PreAuthorize("hasAuthority('teacher_load.update')")
    public ResponseEntity<TeacherLoadResponse> recalculate(@PathVariable Long id) {
        return ResponseEntity.ok(service.recalculatePlannedHours(id));
    }

    @PostMapping("/generate-missing")
    @PreAuthorize("hasAuthority('teacher_load.create')")
    public ResponseEntity<Map<String, Object>> generateMissing(@RequestParam Long periodId) {
        return ResponseEntity.ok(service.generateMissingLoads(periodId));
    }

    @PostMapping("/recalculate-all")
    @PreAuthorize("hasAuthority('teacher_load.update')")
    public ResponseEntity<Map<String, Object>> recalculateAll(@RequestParam Long periodId) {
        return ResponseEntity.ok(service.recalculateAllForPeriod(periodId));
    }
}