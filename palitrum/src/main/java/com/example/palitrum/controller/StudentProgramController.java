package com.example.palitrum.controller;

import com.example.palitrum.dto.StudentEnrollmentDto;
import com.example.palitrum.dto.StudentProgramResponse;
import com.example.palitrum.service.StudentProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-programs")
@RequiredArgsConstructor
public class StudentProgramController {

    private final StudentProgramService service;

    @GetMapping
    @PreAuthorize("hasAuthority('student_program.view')")
    public ResponseEntity<List<StudentProgramResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student_program.create')")
    public ResponseEntity<StudentProgramResponse> create(@RequestBody StudentEnrollmentDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('student_program.update')")
    public ResponseEntity<StudentProgramResponse> update(@PathVariable Long id, @RequestBody StudentEnrollmentDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('student_program.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}