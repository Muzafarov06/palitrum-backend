package com.example.palitrum.controller;

import com.example.palitrum.dto.NewsDTO;
import com.example.palitrum.dto.NewsResponse;
import com.example.palitrum.service.NewsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private static final Logger log = LoggerFactory.getLogger(NewsController.class);
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // ✅ ПУБЛИЧНЫЙ доступ для получения списка новостей
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getFilteredNews(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @PageableDefault(size = 12) Pageable pageable) {

        Pageable pageableWithoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(newsService.getFilteredNews(search, isPublic, pinned, authorId,
                startDate, endDate, pageableWithoutSort));
    }

    // ✅ ПУБЛИЧНЫЙ доступ для просмотра отдельной новости
    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('news.view')")
    public ResponseEntity<Map<String, Long>> getStatistics(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {
        return ResponseEntity.ok(newsService.getStatistics(search, isPublic, pinned, authorId, startDate, endDate));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('news.create')")
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody NewsDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.createNews(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('news.update')")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable Long id, @Valid @RequestBody NewsDTO dto) {
        return ResponseEntity.ok(newsService.updateNews(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('news.delete')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({IllegalArgumentException.class, SecurityException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        log.warn("Error in news request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}