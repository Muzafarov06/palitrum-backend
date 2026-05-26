package com.example.palitrum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.OffsetDateTime;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    FileEntityType entityType;

    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @Column(name = "storage_key", nullable = false)
    String storageKey;  // ключ в S3 (для удаления)

    @Column(name = "file_name", nullable = false)
    String fileName;

    @Column(name = "file_url", nullable = false)
    String fileUrl;

    @Column(name = "file_type")
    String fileType;

    @Column(name = "file_size")
    Long fileSize;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    User uploadedBy;

    @Column(name = "uploaded_at", insertable = false, updatable = false)
    OffsetDateTime uploadedAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    OffsetDateTime updatedAt;
}