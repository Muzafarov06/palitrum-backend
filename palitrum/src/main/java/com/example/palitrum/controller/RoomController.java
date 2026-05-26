package com.example.palitrum.controller;

import com.example.palitrum.dto.RoomDTO;
import com.example.palitrum.dto.RoomResponse;
import com.example.palitrum.service.RoomService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);
    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    // ✅ ПУБЛИЧНЫЙ доступ: получение всех комнат
    @GetMapping
    public ResponseEntity<Page<RoomResponse>> getAllRooms(
            @PageableDefault(size = 100, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.getFilteredRooms(null, null, pageable));
    }

    // ✅ ПУБЛИЧНЫЙ доступ: фильтрация комнат
    @GetMapping("/filter")
    public ResponseEntity<Page<RoomResponse>> filterRooms(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.getFilteredRooms(search, type, pageable));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('room.view')")
    public ResponseEntity<Map<String, Long>> getStatistics(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(service.getStatistics(search, type));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('room.create')")
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('room.update')")
    public ResponseEntity<RoomResponse> update(@PathVariable Long id, @Valid @RequestBody RoomDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('room.delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid room type: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}