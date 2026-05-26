package com.example.palitrum.controller;

import com.example.palitrum.dto.DepartmentDto;
import com.example.palitrum.dto.DepartmentResponse;
import com.example.palitrum.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    // ✅ ПУБЛИЧНЫЙ доступ
    @GetMapping
    public List<DepartmentResponse> getAll() {
        return service.getAll();
    }

    // ✅ ПУБЛИЧНЫЙ доступ
    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('department.create')")
    public DepartmentResponse create(@RequestBody DepartmentDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('department.update')")
    public DepartmentResponse update(@PathVariable Long id, @RequestBody DepartmentDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('department.delete')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}