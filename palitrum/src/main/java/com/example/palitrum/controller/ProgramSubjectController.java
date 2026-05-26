package com.example.palitrum.controller;

import com.example.palitrum.dto.ProgramSubjectDTO;
import com.example.palitrum.dto.ProgramSubjectResponse;
import com.example.palitrum.service.ProgramSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/program-subjects")
@RequiredArgsConstructor
public class ProgramSubjectController {

    private final ProgramSubjectService service;

    @GetMapping("/by-program/{programId}")
    @PreAuthorize("hasAuthority('program.view')")
    public List<ProgramSubjectResponse> getByProgram(@PathVariable Long programId) {
        return service.getByProgram(programId);
    }

    @PostMapping("/link")
    @PreAuthorize("hasAuthority('program_subject.assign')")
    public ResponseEntity<ProgramSubjectResponse> link(@Valid @RequestBody ProgramSubjectDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.link(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('program_subject.update')")
    public ResponseEntity<ProgramSubjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgramSubjectDTO dto) {
        dto.setId(id); // устанавливаем id из пути в DTO
        return ResponseEntity.ok(service.update(dto));
    }

    @DeleteMapping("/unlink")
    @PreAuthorize("hasAuthority('program_subject.unassign')")
    public ResponseEntity<Void> unlink(@RequestParam Long programId,
                                       @RequestParam Long subjectId,
                                       @RequestParam Integer academicYear) {
        service.unlink(programId, subjectId, academicYear);
        return ResponseEntity.noContent().build();
    }
}