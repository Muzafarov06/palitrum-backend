package com.example.palitrum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String imageUrl;           // <-- добавлено
    private List<DepartmentResponse> children;
}