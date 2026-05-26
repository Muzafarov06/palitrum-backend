// ==================== StaffService (ПОЛНЫЙ ИСПРАВЛЕННЫЙ) ====================
package com.example.palitrum.service;

import com.example.palitrum.dto.StaffDto;
import com.example.palitrum.dto.StaffResponse;
import com.example.palitrum.model.Staff;
import com.example.palitrum.model.TeacherLoad;
import com.example.palitrum.model.User;
import com.example.palitrum.model.Position;
import com.example.palitrum.model.AcademicPeriod;
import com.example.palitrum.repository.StaffRepository;
import com.example.palitrum.repository.PositionRepository;
import com.example.palitrum.repository.TeacherLoadRepository;
import com.example.palitrum.repository.AcademicPeriodRepository;
import com.example.palitrum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  // ← исправлен импорт

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final PositionRepository positionRepository;
    private final TeacherLoadRepository teacherLoadRepository;
    private final AcademicPeriodRepository periodRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<StaffResponse> getAll() {
        return staffRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StaffResponse createStaff(StaffDto dto) {
        Position position = positionRepository.findById(dto.getPositionId())
                .orElseThrow(() -> new IllegalArgumentException("Должность не найдена"));

        Staff staff = Staff.builder()
                .userId(dto.getUserId())
                .positionId(dto.getPositionId())
                .rateCount(dto.getRateCount())
                .hireDate(dto.getHireDate())
                .isActive(true)
                .build();
        staff = staffRepository.save(staff);

        AcademicPeriod currentPeriod = periodRepository.findFirstByIsCurrentTrue().orElse(null);
        if (currentPeriod != null && position.getIsTeaching()) {
            BigDecimal maxHours = position.getHoursPerRate().multiply(staff.getRateCount());
            TeacherLoad load = TeacherLoad.builder()
                    .teacherId(dto.getUserId())
                    .academicPeriodId(currentPeriod.getId())
                    .staffId(staff.getId())
                    .weeklyHoursPlanned(BigDecimal.ZERO)
                    .maxWeeklyHours(maxHours)
                    .build();
            teacherLoadRepository.save(load);
        }
        return toResponse(staff);
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffDto dto) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));
        staff.setPositionId(dto.getPositionId());
        staff.setRateCount(dto.getRateCount());
        staff.setHireDate(dto.getHireDate());
        return toResponse(staffRepository.save(staff));
    }

    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));
        staff.setIsActive(false);
        staff.setDismissalDate(LocalDate.now());
        staffRepository.save(staff);
    }

    private StaffResponse toResponse(Staff staff) {
        User user = userRepository.findById(staff.getUserId()).orElse(null);
        Position position = positionRepository.findById(staff.getPositionId()).orElse(null);
        return StaffResponse.builder()
                .id(staff.getId())
                .userId(staff.getUserId())
                .userFullName(user != null ? user.getFirstName() + " " + user.getLastName() : "—")
                .positionId(staff.getPositionId())
                .positionName(position != null ? position.getName() : "—")
                .rateCount(staff.getRateCount())
                .hoursPerRate(position != null ? position.getHoursPerRate() : null)
                .maxHoursPerWeek(position != null ? position.getHoursPerRate().multiply(staff.getRateCount()) : null)
                .hireDate(staff.getHireDate())
                .dismissalDate(staff.getDismissalDate())
                .isActive(staff.getIsActive())
                .build();
    }
}