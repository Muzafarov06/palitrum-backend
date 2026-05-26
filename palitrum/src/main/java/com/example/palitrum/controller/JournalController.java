package com.example.palitrum.controller;

import com.example.palitrum.dto.*;
import com.example.palitrum.service.JournalService;
import com.example.palitrum.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    @GetMapping("/teacher/lessons")
    @PreAuthorize("hasAuthority('lesson.view')")
    public ResponseEntity<List<TeacherLessonDto>> getTeacherLessons(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(journalService.getTeacherLessons(userDetails.getUserId(), start, end));
    }

    @GetMapping("/lessons/{lessonId}/participants")
    @PreAuthorize("hasAuthority('attendance.mark') or hasAuthority('grade.set')")
    public ResponseEntity<List<LessonParticipantResponse>> getParticipants(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(journalService.getParticipantsForTeacher(lessonId, userDetails.getUserId()));
    }

    @PatchMapping("/participants/{id}")
    @PreAuthorize("hasAuthority('attendance.mark') or hasAuthority('grade.set')")
    public ResponseEntity<LessonParticipantResponse> updateParticipant(
            @PathVariable Long id,
            @Valid @RequestBody LessonParticipantUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(journalService.updateParticipant(id, request, userDetails.getUserId()));
    }

    @GetMapping("/student/{studentId}/grades")
    @PreAuthorize("hasAuthority('grade.view') or hasRole('STUDENT')")
    public ResponseEntity<List<StudentGradeDto>> getStudentGrades(@PathVariable Long studentId) {
        return ResponseEntity.ok(journalService.getStudentGrades(studentId));
    }

    @GetMapping("/student/{studentId}/programs")
    @PreAuthorize("hasRole('STUDENT') or hasAuthority('user.view')")
    public ResponseEntity<List<StudentProgramDto>> getStudentPrograms(@PathVariable Long studentId) {
        return ResponseEntity.ok(journalService.getStudentPrograms(studentId));
    }

    @GetMapping("/student/{studentId}/grades/{subjectId}/details")
    @PreAuthorize("hasAuthority('grade.view') or hasRole('STUDENT')")
    public ResponseEntity<List<GradeDetailDto>> getStudentGradeDetails(
            @PathVariable Long studentId,
            @PathVariable Long subjectId,
            @RequestParam(required = false) Long periodId) {
        return ResponseEntity.ok(journalService.getStudentGradeDetails(studentId, subjectId, periodId));
    }
}