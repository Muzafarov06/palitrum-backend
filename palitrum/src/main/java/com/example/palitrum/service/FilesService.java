package com.example.palitrum.service;

import com.example.palitrum.dto.FileCreateDto;
import com.example.palitrum.dto.FileResponseDto;
import com.example.palitrum.model.File;
import com.example.palitrum.model.FileEntityType;
import com.example.palitrum.model.User;
import com.example.palitrum.repository.FilesRepository;
import com.example.palitrum.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilesService {

    private final FilesRepository repo;
    private final UserRepository userRepo;
    private final FileStorageService storageService;

    public FilesService(FilesRepository repo, UserRepository userRepo, FileStorageService storageService) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.storageService = storageService;
    }

    @Transactional
    public FileResponseDto create(FileCreateDto dto) {
        File f = new File();
        f.setEntityType(FileEntityType.valueOf(dto.entityType().toUpperCase()));
        f.setEntityId(dto.entityId());
        f.setStorageKey(dto.storageKey());
        f.setFileName(dto.fileName());
        f.setFileUrl(dto.fileUrl());
        f.setFileType(dto.fileType());
        f.setFileSize(dto.fileSize());
        if (dto.uploadedById() != null) {
            User user = userRepo.findById(dto.uploadedById()).orElse(null);
            f.setUploadedBy(user);
        }
        File saved = repo.save(f);
        return mapToResponse(saved);
    }

    public FileResponseDto getById(Long id) {
        File f = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
        return mapToResponse(f);
    }

    public List<FileResponseDto> listByEntity(FileEntityType entityType, Long entityId) {
        return repo.findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Map<Long, String> getFirstImageUrlForEntities(FileEntityType entityType, List<Long> entityIds) {
        if (entityIds == null || entityIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object[]> result = repo.findFirstFileUrlByEntityTypeAndEntityIdIn(entityType, entityIds);
        return result.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (String) row[1],
                        (existing, replacement) -> existing
                ));
    }

    @Transactional
    public List<FileResponseDto> upload(Long entityId, FileEntityType entityType, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        List<FileResponseDto> result = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                FileStorageService.UploadResult uploadResult = storageService.upload(file);
                FileCreateDto dto = new FileCreateDto(
                        entityType.name(),
                        entityId,
                        uploadResult.key(),
                        uploadResult.originalFilename(),
                        uploadResult.url(),
                        uploadResult.contentType(),
                        uploadResult.size(),
                        null
                );
                result.add(create(dto));
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }
        return result;
    }

    @Transactional
    public void delete(Long id) {
        File file = repo.findById(id).orElse(null);
        if (file != null && file.getStorageKey() != null && !file.getStorageKey().isBlank()) {
            storageService.delete(file.getStorageKey());
        }
        repo.deleteById(id);
    }

    public String getFirstImageUrlForEntity(FileEntityType entityType, Long entityId) {
        Map<Long, String> map = getFirstImageUrlForEntities(entityType, List.of(entityId));
        return map.getOrDefault(entityId, null);
    }

    @Transactional
    public void deleteByEntity(FileEntityType entityType, Long entityId) {
        List<File> files = repo.findByEntityTypeAndEntityId(entityType, entityId);
        for (File file : files) {
            if (file.getStorageKey() != null && !file.getStorageKey().isBlank()) {
                storageService.delete(file.getStorageKey());
            }
        }
        if (!files.isEmpty()) {
            repo.deleteAll(files);
        }
    }

    private FileResponseDto mapToResponse(File f) {
        return new FileResponseDto(
                f.getId(),
                f.getEntityType() != null ? f.getEntityType().name() : "UNKNOWN",
                f.getEntityId(),
                f.getStorageKey(),
                f.getFileName(),
                f.getFileUrl(),
                f.getFileType(),
                f.getFileSize(),
                f.getUploadedBy() != null ? f.getUploadedBy().getId() : null,
                f.getUploadedAt() != null ? f.getUploadedAt().toString() : null,
                f.getUpdatedAt() != null ? f.getUpdatedAt().toString() : null
        );
    }

    // Возвращает все файлы с пагинацией
    public Page<FileResponseDto> getAllFiles(Pageable pageable) {
        try {
            Page<File> page = repo.findAll(pageable);
            return page.map(this::mapToResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty(pageable);
        }
    }

    // Статистика по типам файлов
    public Map<String, Long> getStatistics() {
        try {
            List<Object[]> typeCounts = repo.countGroupByEntityType();
            Map<String, Long> stats = new HashMap<>();
            long total = 0;
            if (typeCounts != null) {
                for (Object[] row : typeCounts) {
                    if (row != null && row.length >= 2) {
                        FileEntityType type = (FileEntityType) row[0];
                        Long count = (Long) row[1];
                        if (type != null && count != null) {
                            stats.put(type.name(), count);
                            total += count;
                        }
                    }
                }
            }
            stats.put("total", total);
            return stats;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Long> errorStats = new HashMap<>();
            errorStats.put("total", 0L);
            return errorStats;
        }
    }
}