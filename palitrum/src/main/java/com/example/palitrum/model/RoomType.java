package com.example.palitrum.model;

public enum RoomType {
    studio,
    hall,
    classroom,
    rehearsal;

    public static RoomType fromString(String value) {
        if (value == null) return null;
        for (RoomType t : RoomType.values()) {
            if (t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Unknown room type: " + value);
    }
}