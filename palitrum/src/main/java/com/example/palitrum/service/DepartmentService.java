package com.example.palitrum.service;

import com.example.palitrum.dto.DepartmentDto;
import com.example.palitrum.dto.DepartmentResponse;
import com.example.palitrum.model.Department;
import com.example.palitrum.model.FileEntityType;
import com.example.palitrum.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository repo;
    private final FilesService filesService;  // добавлено для работы с файлами

    // === Получить весь список (плоский)
    public List<DepartmentResponse> getAll() {
        return repo.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // === Получить по id
    public DepartmentResponse get(Long id) {
        return repo.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    // === Создать
    public DepartmentResponse create(DepartmentDto dto) {
        Department dept = new Department();
        dept.setName(dto.getName());
        dept.setDescription(dto.getDescription());

        if (dto.getParentId() != null) {
            repo.findById(dto.getParentId())
                    .ifPresent(dept::setParent);
        }

        return toResponseDto(repo.save(dept));
    }

    // === Обновить
    public DepartmentResponse update(Long id, DepartmentDto dto) {
        Department dept = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found: " + id));

        dept.setName(dto.getName());
        dept.setDescription(dto.getDescription());

        if (dto.getParentId() != null) {
            repo.findById(dto.getParentId())
                    .ifPresent(dept::setParent);
        } else {
            dept.setParent(null);
        }

        return toResponseDto(repo.save(dept));
    }

    // === Удалить с каскадом
    public void delete(Long id) {
        Department dept = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found: " + id));
        repo.delete(dept);
    }

    // === Маппер (с добавлением imageUrl)
    private DepartmentResponse toResponseDto(Department dept) {
        // Получаем первый файл изображения для этого отделения
        String imageUrl = filesService.listByEntity(FileEntityType.DEPARTMENT, dept.getId())
                .stream()
                .findFirst()
                .map(file -> file.fileUrl())  // fileUrl() – метод record FileResponseDto
                .orElse(null);

        return new DepartmentResponse(
                dept.getId(),
                dept.getName(),
                dept.getDescription(),
                dept.getParent() != null ? dept.getParent().getId() : null,
                imageUrl,    // передаём URL в DTO
                null         // children – заполняются отдельно при необходимости
        );
    }
}