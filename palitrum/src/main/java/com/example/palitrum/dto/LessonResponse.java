package com.example.palitrum.dto;

import com.example.palitrum.model.Lesson;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class LessonResponse {
    private Long id;
    private Long groupId;
    private Long studentId;
    private Long subjectId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Long roomId;
    private Long teacherId;
    private Long substituteTeacherId;
    private Lesson.LessonType lessonType;
    private Lesson.LessonStatus status;
    private String notes;
    private Long createdBy;
    private OffsetDateTime createdAt;

    public LessonResponse() {}

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Long getSubstituteTeacherId() { return substituteTeacherId; }
    public void setSubstituteTeacherId(Long substituteTeacherId) { this.substituteTeacherId = substituteTeacherId; }

    public Lesson.LessonType getLessonType() { return lessonType; }
    public void setLessonType(Lesson.LessonType lessonType) { this.lessonType = lessonType; }

    public Lesson.LessonStatus getStatus() { return status; }
    public void setStatus(Lesson.LessonStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}