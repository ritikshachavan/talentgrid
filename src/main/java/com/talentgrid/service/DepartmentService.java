package com.talentgrid.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talentgrid.dto.DepartmentRequest;
import com.talentgrid.dto.DepartmentResponse;
import com.talentgrid.entity.Department;
import com.talentgrid.exception.DuplicateResourceException;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest r) {
        if (repository.existsByNameIgnoreCase(r.name()))
            throw new DuplicateResourceException("Department already exists: " + r.name());
        Department d = new Department();
        d.setName(r.name());
        d.setDescription(r.description());
        return toResponse(repository.save(d));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest r) {
        Department d = getOrThrow(id);
        if (repository.existsByNameIgnoreCaseAndIdNot(r.name(), id))
            throw new DuplicateResourceException("Department already exists: " + r.name());
        d.setName(r.name());
        d.setDescription(r.description());
        return toResponse(d);   // Hibernate saves changes automatically at the end of the transaction
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Department getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    private DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getDescription());
    }
}