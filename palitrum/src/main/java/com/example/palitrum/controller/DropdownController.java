package com.example.palitrum.controller;

import com.example.palitrum.dto.GroupResponse;
import com.example.palitrum.dto.RoomResponse;
import com.example.palitrum.dto.TeacherDropdownDto;
import com.example.palitrum.dto.UserResponseDto;
import com.example.palitrum.service.DropdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dropdown")
@RequiredArgsConstructor
public class DropdownController {

    private final DropdownService dropdownService;

    @GetMapping("/groups/by-period/{periodId}")
    public List<GroupResponse> getGroupsByPeriod(@PathVariable Long periodId) {
        return dropdownService.getGroupsByPeriod(periodId);
    }

    @GetMapping("/students/individual")
    public List<UserResponseDto> getIndividualStudents() {
        return dropdownService.getIndividualStudents();
    }

    @GetMapping("/teachers")
    public List<TeacherDropdownDto> getAllTeachers() {
        return dropdownService.getAllTeachers();
    }

    @GetMapping("/rooms")
    public List<RoomResponse> getRooms(@RequestParam(defaultValue = "1") int minCapacity) {
        return dropdownService.getRoomsByMinCapacity(minCapacity);
    }
}