package com.example.palitrum.dto;

import java.time.OffsetDateTime;

public class ErrorResponse {
    private OffsetDateTime timestamp;
    private String code;
    private String message;

    public ErrorResponse(OffsetDateTime timestamp, String code, String message) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
