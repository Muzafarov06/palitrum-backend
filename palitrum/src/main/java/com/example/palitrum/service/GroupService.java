package com.example.palitrum.service;

import com.example.palitrum.dto.GroupDTO;
import com.example.palitrum.dto.GroupResponse;
import com.example.palitrum.dto.StudentGroupResponse;
import com.example.palitrum.model.Group;
import com.example.palitrum.model.StudentGroup;
import com.example.palitrum.model.User;
import com.example.palitrum.repository.GroupRepository;
import com.example.palitrum.repository.StudentGroupRepository;
import com.example.palitrum.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository repository;
    private final StudentGroupRepository studentGroupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository repository,
                        StudentGroupRepository studentGroupRepository,
                        UserRepository userRepository) {
        this.repository = repository;
        this.studentGroupRepository = studentGroupRepository;
        this.userRepository = userRepository;
    }

    public List<GroupResponse> getAll() {
        return repository.findAll().stream()
                .map(g -> new GroupResponse(
                        g.getId(),
                        g.getProgramId(),
                        g.getName(),
                        g.getLevel(),
                        g.getMaxStudents(),
                        g.getStatus(),
                        g.getAcademicYear(),
                        g.getSubjectId(),
                        g.getCreatedAt(),
                        g.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    public List<StudentGroupResponse> getStudentsByGroup(Long groupId) {
        return studentGroupRepository.findByGroupIdAndEnrollmentStatus(groupId, StudentGroup.EnrollmentStatus.ENROLLED)
                .stream()
                .map(sg -> {
                    User user = userRepository.findById(sg.getUserId()).orElse(null);
                    String fullName = user != null ? user.getFirstName() + " " + user.getLastName() : "—";
                    StudentGroupResponse resp = new StudentGroupResponse();
                    resp.setId(sg.getId());
                    resp.setUserId(sg.getUserId());
                    resp.setGroupId(sg.getGroupId());
                    resp.setEnrolledDate(sg.getEnrolledDate());
                    resp.setEnrollmentStatus(sg.getEnrollmentStatus().name());
                    resp.setActive(sg.isActive());
                    resp.setUserFullName(fullName);
                    return resp;
                }).collect(Collectors.toList());
    }

    public GroupResponse create(GroupDTO dto) {
        Group group = Group.builder()
                .programId(dto.getProgramId())
                .name(dto.getName())
                .level(dto.getLevel())
                .maxStudents(dto.getMaxStudents())
                .status(dto.getStatus() != null ? dto.getStatus() : "active")
                .academicYear(dto.getAcademicYear())
                .subjectId(dto.getSubjectId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Group saved = repository.save(group);
        return new GroupResponse(
                saved.getId(),
                saved.getProgramId(),
                saved.getName(),
                saved.getLevel(),
                saved.getMaxStudents(),
                saved.getStatus(),
                saved.getAcademicYear(),
                saved.getSubjectId(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}