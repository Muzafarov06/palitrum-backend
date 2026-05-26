package com.example.palitrum.controller;

import com.example.palitrum.dto.ProgramDepartmentDTO;
import com.example.palitrum.dto.ProgramDepartmentResponse;
import com.example.palitrum.service.ProgramDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/program-department")
public class ProgramDepartmentController {

    private final ProgramDepartmentService service;

    @GetMapping("/by-department/{id}")
    @PreAuthorize("hasAuthority('program.view')")
    public List<ProgramDepartmentResponse> getPrograms(@PathVariable Long id) {
        return service.getProgramsByDepartment(id);
    }

    @PostMapping("/link")
    @PreAuthorize("hasAuthority('program_department.assign')")
    public ResponseEntity<?> link(@RequestBody ProgramDepartmentDTO dto) {
        try {
            ProgramDepartmentResponse response = service.link(
                    dto.getProgramId(),
                    dto.getDepartmentId(),
                    dto.getIsPrimary(),
                    dto.getNotes()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Произошла ошибка на сервере"));
        }
    }

    @DeleteMapping("/unlink")
    @PreAuthorize("hasAuthority('program_department.unassign')")
    public ResponseEntity<Void> unlink(@RequestParam Long programId, @RequestParam Long departmentId) {
        service.unlink(programId, departmentId);
        return ResponseEntity.noContent().build();
    }

    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}