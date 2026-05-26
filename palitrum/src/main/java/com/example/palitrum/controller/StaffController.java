package com.example.palitrum.controller;

import com.example.palitrum.dto.StaffDto;
import com.example.palitrum.dto.StaffResponse;
import com.example.palitrum.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService service;

    @GetMapping
    @PreAuthorize("hasAuthority('staff.view')")
    public ResponseEntity<List<StaffResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('staff.create')")
    public ResponseEntity<StaffResponse> create(@Valid @RequestBody StaffDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createStaff(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('staff.update')")
    public ResponseEntity<StaffResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody StaffDto dto) {
        return ResponseEntity.ok(service.updateStaff(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('staff.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}