// ====================== КОНТРОЛЛЕР ======================
package com.example.palitrum.controller;

import com.example.palitrum.dto.PositionDto;
import com.example.palitrum.dto.PositionResponse;
import com.example.palitrum.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService service;

    @GetMapping
    @PreAuthorize("hasAuthority('position.view')")
    public ResponseEntity<List<PositionResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('position.create')")
    public ResponseEntity<PositionResponse> create(@Valid @RequestBody PositionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('position.update')")
    public ResponseEntity<PositionResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody PositionDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('position.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}