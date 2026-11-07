package com.talentgrid.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.talentgrid.dto.EmployeeResponse;
import com.talentgrid.dto.EmployeeSearchCriteria;
import com.talentgrid.dto.PagedResponse;
import com.talentgrid.entity.enums.EmployeeStatus;
import com.talentgrid.service.EmployeeSearchService;

@RestController
@RequestMapping("/api/employees/search")
public class EmployeeSearchController {

    private final EmployeeSearchService service;

    public EmployeeSearchController(EmployeeSearchService service) {
        this.service = service;
    }

    @GetMapping
    public PagedResponse<EmployeeResponse> search(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long skillId,
            @RequestParam(required = false) Integer minProficiency,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minExperience,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return service.search(new EmployeeSearchCriteria(
                departmentId, skillId, minProficiency, status, name, minExperience), pageable);
    }
}