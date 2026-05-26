package com.example.palitrum.service;

import com.example.palitrum.dto.StudentGroupDTO;
import com.example.palitrum.dto.StudentGroupResponse;
import com.example.palitrum.model.StudentGroup;
import com.example.palitrum.model.StudentGroup.EnrollmentStatus;
import com.example.palitrum.repository.StudentGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentGroupService {

    private final StudentGroupRepository repository;

    public StudentGroupService(StudentGroupRepository repository) {
        this.repository = repository;
    }

    public List<StudentGroupResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StudentGroupResponse create(StudentGroupDTO dto) {
        StudentGroup entity = StudentGroup.builder()
                .userId(dto.getUserId())
                .groupId(dto.getGroupId())
                .enrolledDate(dto.getEnrolledDate())
                .leftDate(dto.getLeftDate())
                .enrollmentStatus(EnrollmentStatus.valueOf(dto.getEnrollmentStatus()))
                .active(dto.isActive())
                .sourceApplicationId(dto.getSourceApplicationId())
                .build();
        return toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private StudentGroupResponse toResponse(StudentGroup entity) {
        return new StudentGroupResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getGroupId(),
                entity.getEnrolledDate(),
                entity.getLeftDate(),
                entity.getEnrollmentStatus().name(),
                entity.isActive(),
                entity.getSourceApplicationId()
        );
    }
}