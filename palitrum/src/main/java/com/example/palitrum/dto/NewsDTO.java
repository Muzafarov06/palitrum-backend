package com.example.palitrum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public class NewsDTO {

        @Null(groups = {Create.class, Update.class})
        private Long id;

        @NotBlank(groups = Create.class, message = "Заголовок обязателен")
        @Size(max = 200, groups = {Create.class, Update.class}, message = "Заголовок не более 200 символов")
        private String title;

        @NotBlank(groups = Create.class, message = "Содержание обязательно")
        @Size(max = 10000, groups = {Create.class, Update.class}, message = "Содержание не более 10000 символов")
        private String content;

        @NotNull(groups = Create.class, message = "ID автора обязателен")
        private Long authorId;

        @NotNull(groups = Create.class, message = "Укажите isPublic")
        private Boolean isPublic;

        @NotNull(groups = Create.class, message = "Укажите pinned")
        private Boolean pinned;

        // конструкторы, геттеры, сеттеры
        public NewsDTO() {}

        public NewsDTO(Long id, String title, String content, Long authorId, Boolean isPublic, Boolean pinned) {
                this.id = id;
                this.title = title;
                this.content = content;
                this.authorId = authorId;
                this.isPublic = isPublic;
                this.pinned = pinned;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }

        public Boolean getIsPublic() { return isPublic; }
        public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

        public Boolean getPinned() { return pinned; }
        public void setPinned(Boolean pinned) { this.pinned = pinned; }

        public interface Create {}
        public interface Update {}
}