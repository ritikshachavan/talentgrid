package com.talentgrid.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talentgrid.dto.EmployeeRequest;
import com.talentgrid.dto.EmployeeResponse;
import com.talentgrid.dto.PagedResponse;
import com.talentgrid.entity.Employee;
import com.talentgrid.entity.enums.EmployeeStatus;
import com.talentgrid.exception.DuplicateResourceException;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.DepartmentRepository;
import com.talentgrid.repository.DesignationRepository;
import com.talentgrid.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           DesignationRepository designationRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest r) {
        if (employeeRepository.existsByEmailIgnoreCase(r.email()))
            throw new DuplicateResourceException("Email already in use: " + r.email());
        if (employeeRepository.existsByEmpCodeIgnoreCase(r.empCode()))
            throw new DuplicateResourceException("Employee code already in use: " + r.empCode());

        Employee e = new Employee();
        apply(e, r, null);
        Employee saved = employeeRepository.save(e);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> findAll(Pageable pageable) {
        return PagedResponse.from(employeeRepository.findAll(pageable).map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest r) {
        Employee e = getOrThrow(id);
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(r.email(), id))
            throw new DuplicateResourceException("Email already in use: " + r.email());
        if (employeeRepository.existsByEmpCodeIgnoreCaseAndIdNot(r.empCode(), id))
            throw new DuplicateResourceException("Employee code already in use: " + r.empCode());

        apply(e, r, id);
        return toResponse(e);
    }

    @Transactional
    public void delete(Long id) {
        // soft delete: the row stays, only the status changes
        getOrThrow(id).setStatus(EmployeeStatus.INACTIVE);
    }

    private void apply(Employee e, EmployeeRequest r, Long selfId) {
        e.setEmpCode(r.empCode());
        e.setFirstName(r.firstName());
        e.setLastName(r.lastName());
        e.setEmail(r.email());
        e.setHireDate(r.hireDate());
        e.setExperienceYears(r.experienceYears());
        if (r.status() != null) e.setStatus(r.status());

        e.setDepartment(departmentRepository.findById(r.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", r.departmentId())));
        e.setDesignation(designationRepository.findById(r.designationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation", r.designationId())));

        if (r.managerId() == null) {
            e.setManager(null);
        } else {
            if (r.managerId().equals(selfId))
                throw new IllegalArgumentException("An employee cannot be their own manager");
            e.setManager(employeeRepository.findById(r.managerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager", r.managerId())));
        }
    }

    private Employee getOrThrow(Long id) {
        return employeeRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
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