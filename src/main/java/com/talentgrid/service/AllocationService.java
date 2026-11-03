package com.talentgrid.service;

import java.util.List;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.AllocationRequest;
import com.talentgrid.dto.AllocationResponse;
import com.talentgrid.entity.Allocation;
import com.talentgrid.entity.Employee;
import com.talentgrid.entity.Project;
import com.talentgrid.entity.enums.AllocationStatus;
import com.talentgrid.entity.enums.EmployeeStatus;
import com.talentgrid.exception.OverAllocationException;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.AllocationRepository;
import com.talentgrid.repository.EmployeeRepository;
import com.talentgrid.repository.ProjectRepository;

@Service
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    public AllocationService(AllocationRepository allocationRepository,
                             EmployeeRepository employeeRepository,
                             ProjectRepository projectRepository) {
        this.allocationRepository = allocationRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public AllocationResponse create(AllocationRequest r) {
        Allocation a = new Allocation();
        apply(a, r, null);
        return toResponse(allocationRepository.save(a));
    }

    @Transactional(readOnly = true)
    public List<AllocationResponse> findAll(Long employeeId, Long projectId) {
        List<Allocation> list;
        if (employeeId != null) list = allocationRepository.findByEmployeeId(employeeId);
        else if (projectId != null) list = allocationRepository.findByProjectId(projectId);
        else list = allocationRepository.findAll();
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AllocationResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public AllocationResponse update(Long id, AllocationRequest r) {
        Allocation a = getOrThrow(id);

        // optimistic locking check: the caller edited an old copy
        if (r.version() != null && !r.version().equals(a.getVersion()))
            throw new ObjectOptimisticLockingFailureException(Allocation.class, id);

        apply(a, r, id);
        return toResponse(a);
    }

    // "Release": the row stays for history, status becomes CANCELLED
    @Transactional
    public void release(Long id) {
        getOrThrow(id).setStatus(AllocationStatus.CANCELLED);
    }

    private void apply(Allocation a, AllocationRequest r, Long selfId) {
        if (r.endDate() != null && r.endDate().isBefore(r.startDate()))
            throw new IllegalArgumentException("End date cannot be before start date");

        Employee employee = employeeRepository.findById(r.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", r.employeeId()));
        Project project = projectRepository.findById(r.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", r.projectId()));

        if (employee.getStatus() == EmployeeStatus.INACTIVE)
            throw new IllegalArgumentException("Cannot allocate an inactive employee");

        AllocationStatus newStatus = (r.status() != null) ? r.status()
                : (a.getStatus() != null ? a.getStatus() : AllocationStatus.ACTIVE);

        // The 100% rule only counts ACTIVE allocations
        if (newStatus == AllocationStatus.ACTIVE) {
            Long already = (selfId == null)
                    ? allocationRepository.sumPercent(employee.getId(), AllocationStatus.ACTIVE)
                    : allocationRepository.sumPercentExcluding(employee.getId(), AllocationStatus.ACTIVE, selfId);
            long total = already + r.allocationPercent();
            if (total > 100)
                throw new OverAllocationException(
                        "Over-allocation: " + employee.getFullName() + " is already at " + already
                        + "%, adding " + r.allocationPercent() + "% would make " + total + "%");
        }

        a.setEmployee(employee);
        a.setProject(project);
        a.setRoleName(r.roleName());
        a.setAllocationPercent(r.allocationPercent());
        a.setStartDate(r.startDate());
        a.setEndDate(r.endDate());
        a.setStatus(newStatus);
    }

    private Allocation getOrThrow(Long id) {
        return allocationRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", id));
    }

    private AllocationResponse toResponse(Allocation a) {
        return new AllocationResponse(
                a.getId(), a.getEmployee().getId(), a.getEmployee().getFullName(),
                a.getProject().getId(), a.getProject().getName(), a.getRoleName(),
                a.getAllocationPercent(), a.getStartDate(), a.getEndDate(),
                a.getStatus(), a.getVersion());
    }
}