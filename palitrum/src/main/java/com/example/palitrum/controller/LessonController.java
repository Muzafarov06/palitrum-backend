package com.example.palitrum.controller;

import com.example.palitrum.dto.CalendarEventDto;
import com.example.palitrum.dto.LessonDTO;
import com.example.palitrum.service.LessonService;
import com.example.palitrum.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService service;

    public LessonController(LessonService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('lesson.view')")
    public ResponseEntity<List<LessonDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('lesson.view')")
    public ResponseEntity<LessonDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('lesson.create')")
    public ResponseEntity<LessonDTO> create(@Valid @RequestBody LessonDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/calendar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CalendarEventDto>> getCalendarEvents(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long periodId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("lesson.view_all"));
        if (!isAdmin) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            Long userId = userDetails.getUserId();
            if (studentId == null && teacherId == null) {
                if (userDetails.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_STUDENT"))) {
                    studentId = userId;
                } else if (userDetails.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_TEACHER"))) {
                    teacherId = userId;
                }
            }
        }
        return ResponseEntity.ok(service.getEventsForPeriod(start, end, groupId, studentId, teacherId, programId, periodId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('lesson.update')")
    public ResponseEntity<LessonDTO> update(@PathVariable Long id, @Valid @RequestBody LessonDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('lesson.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}