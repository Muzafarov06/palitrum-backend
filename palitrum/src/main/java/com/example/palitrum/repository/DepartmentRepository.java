package com.example.palitrum.repository;

import com.example.palitrum.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByParentIsNull();     // корневые
    List<Department> findByParentId(Long id);  // дочерние
    Optional<Department> findByName(String name); // поиск по имени
}