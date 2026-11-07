package com.talentgrid.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.EmployeeResponse;
import com.talentgrid.dto.EmployeeSearchCriteria;
import com.talentgrid.dto.PagedResponse;
import com.talentgrid.entity.Employee;
import com.talentgrid.repository.EmployeeSearchRepository;

@Service
public class EmployeeSearchService {

    private final EmployeeSearchRepository repository;

    public EmployeeSearchService(EmployeeSearchRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> search(EmployeeSearchCriteria c, Pageable pageable) {
        return PagedResponse.from(repository.search(c, pageable).map(this::toResponse));
    }

    private EmployeeResponse toResponse(Employee e) {
        Employee m = e.getManager();
        return new EmployeeResponse(
                e.getId(), e.getEmpCode(), e.getFullName(), e.getEmail(),
                e.getHireDate(), e.getExperienceYears(), e.getStatus(),
                e.getDepartment().getId(), e.getDepartment().getName(),
                e.getDesignation().getId(), e.getDesignation().getTitle(),
                m == null ? null : m.getId(),
                m == null ? null : m.getFullName());
    }
}