package com.example.palitrum.controller;

import com.example.palitrum.dto.StudentGroupDTO;
import com.example.palitrum.dto.StudentGroupResponse;
import com.example.palitrum.service.StudentGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-groups")
public class StudentGroupController {

    private final StudentGroupService service;

    public StudentGroupController(StudentGroupService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('student_group.view')")
    public ResponseEntity<List<StudentGroupResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student_group.add')")
    public ResponseEntity<StudentGroupResponse> create(@RequestBody StudentGroupDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('student_group.remove')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}