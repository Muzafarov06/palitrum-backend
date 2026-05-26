package com.example.palitrum.service;

import com.example.palitrum.dto.NewsDTO;
import com.example.palitrum.dto.NewsResponse;
import com.example.palitrum.model.FileEntityType;
import com.example.palitrum.model.News;
import com.example.palitrum.model.User;
import com.example.palitrum.repository.NewsRepository;
import com.example.palitrum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final FilesService filesService;
    private final SimpMessagingTemplate messagingTemplate;

    private boolean canViewAllNews() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("news.view"));
    }

    @Transactional(readOnly = true)
    public Page<NewsResponse> getFilteredNews(String search, Boolean isPublic, Boolean pinned,
                                              Long authorId, OffsetDateTime startDate,
                                              OffsetDateTime endDate, Pageable pageable) {
        if (!canViewAllNews()) {
            isPublic = true;
            authorId = null;
        }
        Page<News> page = newsRepository.findAllWithFilters(search, isPublic, pinned, authorId, startDate, endDate, pageable);
        if (page.isEmpty()) {
            return page.map(news -> toResponse(news, null));
        }

        List<Long> newsIds = page.getContent().stream().map(News::getId).collect(Collectors.toList());
        Map<Long, String> imageUrlMap = filesService.getFirstImageUrlForEntities(FileEntityType.NEWS, newsIds);

        return page.map(news -> toResponse(news, imageUrlMap.get(news.getId())));
    }

    @Transactional(readOnly = true)
    public NewsResponse getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Новость не найдена"));
        if (!news.getIsPublic() && !canViewAllNews()) {
            throw new SecurityException("Нет доступа к этой новости");
        }
        Map<Long, String> imageUrlMap = filesService.getFirstImageUrlForEntities(FileEntityType.NEWS, List.of(id));
        String imageUrl = imageUrlMap.get(id);
        return toResponse(news, imageUrl);
    }

    @Cacheable(value = "newsStatistics", key = "{#search, #isPublic, #pinned, #authorId, #startDate, #endDate}")
    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics(String search, Boolean isPublic, Boolean pinned,
                                           Long authorId, OffsetDateTime startDate, OffsetDateTime endDate) {
        long total = newsRepository.countWithFilters(search, isPublic, pinned, authorId, startDate, endDate);
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        return stats;
    }

    @CacheEvict(value = "newsStatistics", allEntries = true)
    @Transactional
    public NewsResponse createNews(NewsDTO dto) {
        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Автор не найден"));
        News news = News.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .isPublic(dto.getIsPublic())
                .pinned(dto.getPinned())
                .publishedAt(OffsetDateTime.now())
                .build();
        News saved = newsRepository.save(news);
        log.info("Created news: id={}", saved.getId());

        NewsResponse response = toResponse(saved, null);
        messagingTemplate.convertAndSend("/topic/news", response);
        return response;
    }

    @CacheEvict(value = "newsStatistics", allEntries = true)
    @Transactional
    public NewsResponse updateNews(Long id, NewsDTO dto) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Новость не найдена"));
        if (dto.getTitle() != null) news.setTitle(dto.getTitle());
        if (dto.getContent() != null) news.setContent(dto.getContent());
        if (dto.getIsPublic() != null) news.setIsPublic(dto.getIsPublic());
        if (dto.getPinned() != null) news.setPinned(dto.getPinned());
        News saved = newsRepository.save(news);
        log.info("Updated news: id={}", saved.getId());

        Map<Long, String> imageUrlMap = filesService.getFirstImageUrlForEntities(FileEntityType.NEWS, List.of(id));
        String imageUrl = imageUrlMap.get(id);
        NewsResponse response = toResponse(saved, imageUrl);
        messagingTemplate.convertAndSend("/topic/news", response);
        return response;
    }

    @CacheEvict(value = "newsStatistics", allEntries = true)
    @Transactional
    public void deleteNews(Long id) {
        filesService.deleteByEntity(FileEntityType.NEWS, id);
        newsRepository.deleteById(id);
        log.info("Deleted news: id={}", id);
    }

    private NewsResponse toResponse(News news, String imageUrl) {
        String authorName = news.getAuthor() != null
                ? (news.getAuthor().getFirstName() + " " + news.getAuthor().getLastName())
                : null;
        return new NewsResponse(
                news.getId(),
                news.getTitle(),
                news.getContent(),
                news.getAuthor() != null ? news.getAuthor().getId() : null,
                authorName,
                news.getPublishedAt(),
                news.getIsPublic(),
                news.getPinned(),
                news.getCreatedAt(),
                news.getUpdatedAt(),
                imageUrl
        );
    }
}