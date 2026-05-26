package com.example.palitrum.service;

import com.example.palitrum.dto.RoomDTO;
import com.example.palitrum.dto.RoomResponse;
import com.example.palitrum.model.FileEntityType;
import com.example.palitrum.model.Room;
import com.example.palitrum.model.RoomType;
import com.example.palitrum.repository.RoomRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);
    private final RoomRepository repository;
    private final FilesService filesService;

    public RoomService(RoomRepository repository,
                       FilesService filesService) {
        this.repository = repository;
        this.filesService = filesService;
    }

    @Transactional(readOnly = true)
    public Page<RoomResponse> getFilteredRooms(String search, String type, Pageable pageable) {
        // Вместо null передаём пустую строку, чтобы избежать проблемы с типом параметра
        String searchParam = (search == null || search.isBlank()) ? "" : search;
        RoomType typeParam = null;
        if (type != null && !type.isBlank()) {
            try {
                typeParam = RoomType.fromString(type);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid room type filter: {}", type);
                return Page.empty(pageable);
            }
        }

        Page<Room> page;
        if (typeParam != null) {
            page = repository.findAllByTypeAndSearch(typeParam, searchParam, pageable);
        } else {
            page = repository.findAllBySearch(searchParam, pageable);
        }

        if (page.isEmpty()) return page.map(this::toResponseWithoutImage);

        List<Long> roomIds = page.getContent().stream().map(Room::getId).collect(Collectors.toList());
        Map<Long, String> imageUrlMap = filesService.getFirstImageUrlForEntities(FileEntityType.ROOM, roomIds);

        return page.map(room -> {
            String imageUrl = imageUrlMap.get(room.getId());
            return new RoomResponse(room.getId(), room.getName(), room.getType().name(), room.getCapacity(), imageUrl);
        });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "roomStatistics", key = "{#search, #type}")
    public Map<String, Long> getStatistics(String search, String type) {
        String searchParam = (search == null || search.isBlank()) ? "" : search;
        RoomType typeParam = null;
        if (type != null && !type.isBlank()) {
            try {
                typeParam = RoomType.fromString(type);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid room type filter for statistics: {}", type);
                Map<String, Long> emptyStats = new HashMap<>();
                emptyStats.put("total", 0L);
                return emptyStats;
            }
        }

        long total;
        List<Object[]> typeCounts;
        if (typeParam != null) {
            total = repository.countByTypeAndSearch(typeParam, searchParam);
            typeCounts = repository.countGroupByTypeWithTypeFilter(typeParam, searchParam);
        } else {
            total = repository.countBySearch(searchParam);
            typeCounts = repository.countGroupByTypeNoTypeFilter(searchParam);
        }

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        for (Object[] row : typeCounts) {
            RoomType roomType = (RoomType) row[0];
            Long count = (Long) row[1];
            stats.put(roomType.name(), count);
        }
        return stats;
    }

    @CacheEvict(value = "roomStatistics", allEntries = true)
    @Transactional
    public RoomResponse create(RoomDTO dto) {
        Room room = Room.builder()
                .name(dto.getName())
                .type(RoomType.fromString(dto.getType()))
                .capacity(dto.getCapacity())
                .build();
        Room saved = repository.save(room);
        log.info("Created room: id={}, name={}", saved.getId(), saved.getName());
        return toResponseWithoutImage(saved);
    }

    @CacheEvict(value = "roomStatistics", allEntries = true)
    @Transactional
    public RoomResponse update(Long id, RoomDTO dto) {
        Room room = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found: " + id));
        room.setName(dto.getName());
        room.setType(RoomType.fromString(dto.getType()));
        room.setCapacity(dto.getCapacity());
        Room updated = repository.save(room);
        log.info("Updated room: id={}", updated.getId());
        return toResponseWithoutImage(updated);
    }

    @CacheEvict(value = "roomStatistics", allEntries = true)
    @Transactional
    public void delete(Long id) {
        filesService.deleteByEntity(FileEntityType.ROOM, id);
        repository.deleteById(id);
        log.info("Deleted room: id={}", id);
    }

    private RoomResponse toResponseWithoutImage(Room room) {
        return new RoomResponse(room.getId(), room.getName(), room.getType().name(), room.getCapacity(), null);
    }
}