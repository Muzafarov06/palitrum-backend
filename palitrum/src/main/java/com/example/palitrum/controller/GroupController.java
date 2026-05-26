package com.example.palitrum.controller;

import com.example.palitrum.dto.GroupDTO;
import com.example.palitrum.dto.GroupResponse;
import com.example.palitrum.dto.StudentGroupResponse;
import com.example.palitrum.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService service;

    public GroupController(GroupService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('group.view')")
    public ResponseEntity<List<GroupResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('group.create')")
    public ResponseEntity<GroupResponse> create(@RequestBody GroupDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{groupId}/students")
    @PreAuthorize("hasAuthority('group.view')")
    public ResponseEntity<List<StudentGroupResponse>> getStudents(@PathVariable Long groupId) {
        return ResponseEntity.ok(service.getStudentsByGroup(groupId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('group.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}