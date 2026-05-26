package com.example.palitrum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SystemSettings {
    @Id
    private Integer id = 1;

    @Column(columnDefinition = "jsonb")
    // Убрали @Type(type = "jsonb") — храним как обычную строку,
    // преобразование JSON делаем в сервисе через ObjectMapper
    private String settings;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}