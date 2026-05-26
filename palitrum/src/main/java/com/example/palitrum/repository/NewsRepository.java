package com.example.palitrum.repository;

import com.example.palitrum.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query(value = "SELECT n.* FROM muzafarov_fg.news n WHERE " +
            "(CAST(:search AS text) IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))) AND " +
            "(CAST(:isPublic AS boolean) IS NULL OR n.is_public = :isPublic) AND " +
            "(CAST(:pinned AS boolean) IS NULL OR n.pinned = :pinned) AND " +
            "(CAST(:authorId AS bigint) IS NULL OR n.author_id = :authorId) AND " +
            "(CAST(:startDate AS timestamptz) IS NULL OR n.created_at >= :startDate) AND " +
            "(CAST(:endDate AS timestamptz) IS NULL OR n.created_at <= :endDate) " +
            "ORDER BY n.pinned DESC, n.created_at DESC",   // <-- только эта сортировка
            countQuery = "SELECT COUNT(*) FROM muzafarov_fg.news n WHERE " +
                    "(CAST(:search AS text) IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))) AND " +
                    "(CAST(:isPublic AS boolean) IS NULL OR n.is_public = :isPublic) AND " +
                    "(CAST(:pinned AS boolean) IS NULL OR n.pinned = :pinned) AND " +
                    "(CAST(:authorId AS bigint) IS NULL OR n.author_id = :authorId) AND " +
                    "(CAST(:startDate AS timestamptz) IS NULL OR n.created_at >= :startDate) AND " +
                    "(CAST(:endDate AS timestamptz) IS NULL OR n.created_at <= :endDate)",
            nativeQuery = true)
    Page<News> findAllWithFilters(@Param("search") String search,
                                  @Param("isPublic") Boolean isPublic,
                                  @Param("pinned") Boolean pinned,
                                  @Param("authorId") Long authorId,
                                  @Param("startDate") OffsetDateTime startDate,
                                  @Param("endDate") OffsetDateTime endDate,
                                  Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM muzafarov_fg.news n WHERE " +
            "(CAST(:search AS text) IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))) AND " +
            "(CAST(:isPublic AS boolean) IS NULL OR n.is_public = :isPublic) AND " +
            "(CAST(:pinned AS boolean) IS NULL OR n.pinned = :pinned) AND " +
            "(CAST(:authorId AS bigint) IS NULL OR n.author_id = :authorId) AND " +
            "(CAST(:startDate AS timestamptz) IS NULL OR n.created_at >= :startDate) AND " +
            "(CAST(:endDate AS timestamptz) IS NULL OR n.created_at <= :endDate)",
            nativeQuery = true)
    long countWithFilters(@Param("search") String search,
                          @Param("isPublic") Boolean isPublic,
                          @Param("pinned") Boolean pinned,
                          @Param("authorId") Long authorId,
                          @Param("startDate") OffsetDateTime startDate,
                          @Param("endDate") OffsetDateTime endDate);
}