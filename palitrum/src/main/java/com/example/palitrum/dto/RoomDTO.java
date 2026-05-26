package com.example.palitrum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RoomDTO {

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Тип помещения обязателен")
    private String type;

    @Min(value = 1, message = "Вместимость должна быть положительной")
    private int capacity;

    // конструкторы, геттеры, сеттеры (оставляем без изменений)
    public RoomDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}