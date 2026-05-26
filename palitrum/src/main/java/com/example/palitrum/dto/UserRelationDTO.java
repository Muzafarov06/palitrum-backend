package com.example.palitrum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRelationDTO {

    private Long id;
    private Long parentUserId;
    private Long childUserId;
    private String relationType;
    private boolean verified;
    private LocalDateTime createdAt;
}