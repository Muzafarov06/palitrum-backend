package com.example.palitrum.controller;

import com.example.palitrum.dto.ProgramDto;
import com.example.palitrum.dto.ProgramResponseDto;
import com.example.palitrum.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService service;

    // Публичный эндпоинт для получения списка программ (без авторизации)
    @GetMapping("/public")
    public List<ProgramResponseDto> getAllPublic() {
        return service.listAll();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('program.view')")
    public List<ProgramResponseDto> all() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('program.view')")
    public ProgramResponseDto get(@PathVariable Long id) {
        return service.getById(id);
    }

    // ✅ Сделать публичным
    @GetMapping("/by-department/{departmentId}")
    public List<ProgramResponseDto> getByDepartment(@PathVariable Long departmentId) {
        return service.getByDepartmentId(departmentId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('program.create')")
    public ProgramResponseDto create(@RequestBody ProgramDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('program.update')")
    public ProgramResponseDto update(@PathVariable Long id, @RequestBody ProgramDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('program.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}