package com.example.palitrum.service;

import com.example.palitrum.dto.ProgramDto;
import com.example.palitrum.dto.ProgramResponseDto;
import com.example.palitrum.model.FileEntityType;
import com.example.palitrum.model.Program;
import com.example.palitrum.model.ProgramDepartment;
import com.example.palitrum.repository.ProgramDepartmentRepository;
import com.example.palitrum.repository.ProgramRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ProgramDepartmentRepository programDepartmentRepository;
    private final FilesService filesService;

    public ProgramService(ProgramRepository programRepository,
                          ProgramDepartmentRepository programDepartmentRepository,
                          FilesService filesService) {
        this.programRepository = programRepository;
        this.programDepartmentRepository = programDepartmentRepository;
        this.filesService = filesService;
    }

    public ProgramResponseDto create(ProgramDto dto) {
        Program p = new Program();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setDurationYears(dto.getDurationYears() != null ? dto.getDurationYears() : 4);

        Program saved = programRepository.save(p);
        return toResponse(saved);
    }

    public List<ProgramResponseDto> listAll() {
        return programRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProgramResponseDto getById(Long id) {
        return programRepository.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    public List<ProgramResponseDto> getByDepartmentId(Long departmentId) {
        // ИСПРАВЛЕНО: было findByDepartment_Id, стало findByDepartmentId
        List<ProgramDepartment> links = programDepartmentRepository.findByDepartmentId(departmentId);

        return links.stream()
                .map(ProgramDepartment::getProgram)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProgramResponseDto update(Long id, ProgramDto dto) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found: " + id));

        program.setName(dto.getName());
        program.setDescription(dto.getDescription());
        program.setDurationYears(dto.getDurationYears() != null ? dto.getDurationYears() : program.getDurationYears());

        return toResponse(programRepository.save(program));
    }

    public void delete(Long id) {
        programRepository.deleteById(id);
    }

    private ProgramResponseDto toResponse(Program p) {
        ProgramResponseDto r = new ProgramResponseDto();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setDurationYears(p.getDurationYears());

        if (p.getCreatedAt() != null) {
            r.setCreatedAt(p.getCreatedAt().atOffset(java.time.ZoneOffset.UTC));
        }

        String imageUrl = filesService.listByEntity(FileEntityType.PROGRAM, p.getId())
                .stream()
                .findFirst()
                .map(file -> file.fileUrl())
                .orElse(null);
        r.setImageUrl(imageUrl);

        return r;
    }
}