package com.example.palitrum.controller;

import com.example.palitrum.dto.ScheduleTemplateDto;
import com.example.palitrum.dto.ScheduleTemplateResponse;
import com.example.palitrum.service.ScheduleTemplateService;
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
@RequestMapping("/api/schedule-templates")
@RequiredArgsConstructor
public class ScheduleTemplateController {

    private final ScheduleTemplateService service;

    @GetMapping
    @PreAuthorize("hasAuthority('schedule_template.view')")
    public ResponseEntity<Page<ScheduleTemplateResponse>> getAllByPeriod(
            @RequestParam Long periodId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.getAllByPeriod(periodId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('schedule_template.create')")
    public ResponseEntity<ScheduleTemplateResponse> create(@Valid @RequestBody ScheduleTemplateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('schedule_template.update')")
    public ResponseEntity<ScheduleTemplateResponse> update(@PathVariable Long id,
                                                           @Valid @RequestBody ScheduleTemplateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('schedule_template.generate')")
    public ResponseEntity<?> generateLessons(@RequestParam Long periodId) {
        Map<String, Object> result = service.generateLessonsWithErrors(periodId);
        if (result.containsKey("errors")) {
            // Ошибки валидации/конфликтов – возвращаем 400 с деталями
            return ResponseEntity.badRequest().body(result);
        }
        // Успех – возвращаем {"generated": N}
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('schedule_template.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}