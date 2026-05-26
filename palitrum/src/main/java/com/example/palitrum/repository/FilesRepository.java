package com.example.palitrum.repository;

import com.example.palitrum.model.File;
import com.example.palitrum.model.FileEntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilesRepository extends JpaRepository<File, Long> {

    List<File> findByEntityTypeAndEntityId(FileEntityType entityType, Long entityId);

    Page<File> findAll(Pageable pageable);

    @Query("SELECT f.entityType, COUNT(f) FROM File f GROUP BY f.entityType")
    List<Object[]> countGroupByEntityType();

    @Query("SELECT f.entityId, f.fileUrl FROM File f WHERE f.entityType = :entityType AND f.entityId IN :entityIds ORDER BY f.id ASC")
    List<Object[]> findFirstFileUrlByEntityTypeAndEntityIdIn(@Param("entityType") FileEntityType entityType,
                                                             @Param("entityIds") List<Long> entityIds);
}