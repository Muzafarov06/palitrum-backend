// ====================== СЕРВИС ======================
package com.example.palitrum.service;

import com.example.palitrum.dto.PositionDto;
import com.example.palitrum.dto.PositionResponse;
import com.example.palitrum.model.Position;
import com.example.palitrum.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {

    private final PositionRepository repository;

    @Transactional(readOnly = true)
    public List<PositionResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PositionResponse create(PositionDto dto) {
        Position position = Position.builder()
                .name(dto.getName())
                .hoursPerRate(dto.getHoursPerRate())
                .isTeaching(dto.getIsTeaching() != null ? dto.getIsTeaching() : true)
                .build();
        return toResponse(repository.save(position));
    }

    public PositionResponse update(Long id, PositionDto dto) {
        Position position = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Должность не найдена"));
        position.setName(dto.getName());
        position.setHoursPerRate(dto.getHoursPerRate());
        if (dto.getIsTeaching() != null) {
            position.setIsTeaching(dto.getIsTeaching());
        }
        return toResponse(repository.save(position));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private PositionResponse toResponse(Position p) {
        return PositionResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .hoursPerRate(p.getHoursPerRate())
                .isTeaching(p.getIsTeaching())
                .build();
    }
}