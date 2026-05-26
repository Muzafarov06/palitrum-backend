package com.example.palitrum.service;

import com.example.palitrum.dto.ProgramDepartmentResponse;
import com.example.palitrum.model.ProgramDepartment;
import com.example.palitrum.model.Program;
import com.example.palitrum.model.Department;
import com.example.palitrum.repository.ProgramDepartmentRepository;
import com.example.palitrum.repository.ProgramRepository;
import com.example.palitrum.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramDepartmentService {

    private final ProgramDepartmentRepository repo;
    private final ProgramRepository programRepo;
    private final DepartmentRepository departmentRepo;

    public List<ProgramDepartmentResponse> getProgramsByDepartment(Long departmentId) {
        // ИСПРАВЛЕНО: findByDepartment_Id -> findByDepartmentId
        return repo.findByDepartmentId(departmentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProgramDepartmentResponse link(Long programId, Long departmentId, Boolean primary, String notes) {

        Program program = programRepo.findById(programId)
                .orElseThrow(() -> new RuntimeException("Программа не найдена: " + programId));

        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Отделение не найдено: " + departmentId));

        // Проверка на существующую связь через ID
        if (repo.existsByProgramIdAndDepartmentId(programId, departmentId)) {
            throw new IllegalArgumentException("Эта программа уже добавлена в это отделение!");
        }

        ProgramDepartment pd = new ProgramDepartment();
        pd.setProgram(program);
        pd.setDepartment(department);
        pd.setIsPrimary(primary != null ? primary : false);
        pd.setNotes(notes);

        try {
            return toResponse(repo.save(pd));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Эта программа уже добавлена в это отделение!");
        }
    }

    public void unlink(Long programId, Long departmentId) {
        // ИСПРАВЛЕНО: deleteByProgram_IdAndDepartment_Id -> deleteByProgramIdAndDepartmentId
        repo.deleteByProgramIdAndDepartmentId(programId, departmentId);
    }

    private ProgramDepartmentResponse toResponse(ProgramDepartment pd) {
        return new ProgramDepartmentResponse(
                pd.getId(),
                pd.getProgram().getId(),
                pd.getDepartment().getId(),
                pd.getIsPrimary(),
                pd.getNotes()
        );
    }
}