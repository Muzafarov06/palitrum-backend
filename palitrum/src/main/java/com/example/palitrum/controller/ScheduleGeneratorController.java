package com.example.palitrum.controller;

import com.example.palitrum.dto.ScheduleGenerationRequest;
import com.example.palitrum.service.ScheduleAutoGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/schedule-generator")
@RequiredArgsConstructor
public class ScheduleGeneratorController {

    private final ScheduleAutoGeneratorService generatorService;

    @PostMapping("/generate")
    // @PreAuthorize("hasAnyAuthority('schedule.generate', 'schedule_template.create')") // временно закомментировано
    public ResponseEntity<Map<String, Object>> generateTemplates(@RequestBody ScheduleGenerationRequest request) {
        Map<String, Object> result = generatorService.generateScheduleTemplates(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/smart-generate")
    // @PreAuthorize("hasAnyAuthority('schedule.generate', 'schedule_template.create')") // временно закомментировано
    public ResponseEntity<Map<String, Object>> smartGenerate(@RequestBody ScheduleGenerationRequest request) {
        Map<String, Object> result = generatorService.smartGenerate(request);
        return ResponseEntity.ok(result);
    }
}