package com.example.palitrum.controller;

import com.example.palitrum.dto.SubjectDTO;
import com.example.palitrum.dto.SubjectResponse;
import com.example.palitrum.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService service;

    @GetMapping("/public")
    public List<SubjectResponse> getAllPublic() {
        return service.getAll();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('subject.view')")
    public List<SubjectResponse> all() {
        return service.getAll();
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('subject.view')")
    public ResponseEntity<Page<SubjectResponse>> filterSubjects(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 12, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.getFilteredSubjects(search, pageable));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('subject.view')")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        return ResponseEntity.ok(service.getStatistics());
    }

    @GetMapping("/by-program/{programId}")
    @PreAuthorize("hasAuthority('subject.view')")
    public ResponseEntity<List<SubjectResponse>> getSubjectsByProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(service.getSubjectsByProgram(programId));
    }

    // НОВЫЙ ЭНДПОИНТ: предметы по студенту
    @GetMapping("/by-student/{studentId}")
    @PreAuthorize("hasAuthority('subject.view')")
    public ResponseEntity<List<SubjectResponse>> getSubjectsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(service.getSubjectsByStudent(studentId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('subject.view')")
    public SubjectResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('subject.create')")
    public SubjectResponse create(@RequestBody SubjectDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('subject.update')")
    public SubjectResponse update(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('subject.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}