package com.talentgrid.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.DesignationRequest;
import com.talentgrid.dto.DesignationResponse;
import com.talentgrid.entity.Designation;
import com.talentgrid.exception.DuplicateResourceException;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.DesignationRepository;

@Service
public class DesignationService {

    private final DesignationRepository repository;

    public DesignationService(DesignationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DesignationResponse create(DesignationRequest r) {
        if (repository.existsByTitleIgnoreCase(r.title()))
            throw new DuplicateResourceException("Designation already exists: " + r.title());
        Designation d = new Designation();
        d.setTitle(r.title());
        d.setLevel(r.level());
        return toResponse(repository.save(d));
    }

    @Transactional(readOnly = true)
    public List<DesignationResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DesignationResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public DesignationResponse update(Long id, DesignationRequest r) {
        Designation d = getOrThrow(id);
        if (repository.existsByTitleIgnoreCaseAndIdNot(r.title(), id))
            throw new DuplicateResourceException("Designation already exists: " + r.title());
        d.setTitle(r.title());
        d.setLevel(r.level());
        return toResponse(d);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Designation getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", id));
    }

    private DesignationResponse toResponse(Designation d) {
        return new DesignationResponse(d.getId(), d.getTitle(), d.getLevel());
    }
}