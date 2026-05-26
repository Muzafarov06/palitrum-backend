package com.example.palitrum.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE,
    EXCUSED;

    @JsonValue
    public String getValue() {
        return name();
    }
}