package com.example.palitrum.controller;

import com.example.palitrum.dto.FileCreateDto;
import com.example.palitrum.dto.FileResponseDto;
import com.example.palitrum.model.FileEntityType;
import com.example.palitrum.service.FilesService;
import com.example.palitrum.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FilesController {

    private final FilesService filesService;
    private final FileStorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "uploadedById", required = false) Long uploadedById
    ) {
        try {
            List<FileResponseDto> result = new ArrayList<>();
            for (MultipartFile file : files) {
                var uploadRes = storageService.upload(file);
                FileCreateDto dto = new FileCreateDto(
                        entityType,
                        entityId,
                        uploadRes.key(),
                        file.getOriginalFilename(),
                        uploadRes.url(),
                        file.getContentType(),
                        file.getSize(),
                        uploadedById
                );
                result.add(filesService.create(dto));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/replace/{entityType}/{entityId}")
    public ResponseEntity<?> replaceFiles(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "uploadedById", required = false) Long uploadedById
    ) {
        try {
            FileEntityType type = FileEntityType.valueOf(entityType.toUpperCase());
            List<FileResponseDto> oldFiles = filesService.listByEntity(type, entityId);
            for (FileResponseDto old : oldFiles) {
                if (old.storageKey() != null) storageService.delete(old.storageKey());
                filesService.delete(old.id());
            }
            List<FileResponseDto> result = new ArrayList<>();
            for (MultipartFile file : files) {
                var uploadRes = storageService.upload(file);
                FileCreateDto dto = new FileCreateDto(
                        entityType,
                        entityId,
                        uploadRes.key(),
                        file.getOriginalFilename(),
                        uploadRes.url(),
                        file.getContentType(),
                        file.getSize(),
                        uploadedById
                );
                result.add(filesService.create(dto));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Эндпоинт для получения всех файлов с пагинацией (используется в админке)
    @GetMapping("/list")
    public ResponseEntity<Page<FileResponseDto>> getAllFiles(
            @PageableDefault(size = 12, sort = "uploadedAt") Pageable pageable) {
        return ResponseEntity.ok(filesService.getAllFiles(pageable));
    }

    // Статистика по типам файлов
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        return ResponseEntity.ok(filesService.getStatistics());
    }

    @PostMapping(value = "/upload/public", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPublic(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") Long entityId
    ) {
        try {
            List<FileResponseDto> result = new ArrayList<>();
            for (MultipartFile file : files) {
                var uploadRes = storageService.upload(file);
                FileCreateDto dto = new FileCreateDto(
                        entityType,
                        entityId,
                        uploadRes.key(),
                        file.getOriginalFilename(),
                        uploadRes.url(),
                        file.getContentType(),
                        file.getSize(),
                        null
                );
                result.add(filesService.create(dto));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Ошибка загрузки файлов: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{entityType}/{entityId}")
    public List<FileResponseDto> getFiles(
            @PathVariable String entityType,
            @PathVariable Long entityId
    ) {
        FileEntityType type = FileEntityType.valueOf(entityType.toUpperCase());
        return filesService.listByEntity(type, entityId);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Void> downloadFile(@PathVariable Long id) {
        FileResponseDto file = filesService.getById(id);
        String fileUrl = file.fileUrl();
        if (fileUrl != null && !fileUrl.isBlank()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(fileUrl))
                    .build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
            FileResponseDto file = filesService.getById(id);
            String key = file.storageKey();
            if (key != null && !key.isBlank()) {
                storageService.delete(key);
            }
            filesService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}