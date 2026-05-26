package com.example.palitrum.dto;

import java.time.OffsetDateTime;

public class NewsResponse {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private OffsetDateTime publishedAt;
    private Boolean isPublic;
    private Boolean pinned;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String imageUrl;          // новое поле

    public NewsResponse() {}

    public NewsResponse(Long id, String title, String content, Long authorId, String authorName,
                        OffsetDateTime publishedAt, Boolean isPublic, Boolean pinned,
                        OffsetDateTime createdAt, OffsetDateTime updatedAt, String imageUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.publishedAt = publishedAt;
        this.isPublic = isPublic;
        this.pinned = pinned;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imageUrl = imageUrl;
    }

    // геттеры и сеттеры (все обязательные)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public OffsetDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(OffsetDateTime publishedAt) { this.publishedAt = publishedAt; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}