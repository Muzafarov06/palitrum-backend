package com.example.palitrum.service;

import com.example.palitrum.dto.*;
import com.example.palitrum.model.*;
import com.example.palitrum.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DropdownService {

    private final AcademicPeriodRepository periodRepository;
    private final GroupRepository groupRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;
    private final UserService userService;
    private final StaffRepository staffRepository;                // новое
    private final PositionRepository positionRepository;          // новое

    public List<GroupResponse> getGroupsByPeriod(Long periodId) {
        List<Group> groups = groupRepository.findByStatus("active");
        return groups.stream()
                .map(this::toGroupResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> getIndividualStudents() {
        List<Long> studentIds = studentSubjectRepository.findStudentIdsWithIndividualNoTeacher();
        if (studentIds.isEmpty()) return List.of();
        List<User> students = userRepository.findAllById(studentIds);
        return students.stream()
                .map(userService::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Возвращает преподавателей с информацией о должности и staffId */
    public List<TeacherDropdownDto> getAllTeachers() {
        List<User> teachers = userRepository.findTeachers();
        return teachers.stream().map(user -> {
            Staff staff = staffRepository.findByUserIdAndIsActiveTrue(user.getId()).orElse(null);
            return TeacherDropdownDto.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .staffId(staff != null ? staff.getId() : null)
                    .staffPosition(staff != null
                            ? positionRepository.findById(staff.getPositionId())
                            .map(Position::getName).orElse(null)
                            : null)
                    .staffRate(staff != null ? staff.getRateCount() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<RoomResponse> getRoomsByMinCapacity(int minCapacity) {
        return roomRepository.findByCapacityMin(minCapacity).stream()
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    private GroupResponse toGroupResponse(Group group) {
        GroupResponse dto = new GroupResponse();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setProgramId(group.getProgramId());
        dto.setAcademicYear(group.getAcademicYear());
        dto.setSubjectId(group.getSubjectId());
        dto.setMaxStudents(group.getMaxStudents());
        dto.setStatus(group.getStatus());
        return dto;
    }

    private RoomResponse toRoomResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getType().name(),
                room.getCapacity(),
                null
        );
    }
}