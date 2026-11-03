package com.talentgrid.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentgrid.dto.EmployeeSkillRequest;
import com.talentgrid.dto.EmployeeSkillResponse;
import com.talentgrid.dto.EmployeeSkillUpdateRequest;
import com.talentgrid.service.EmployeeSkillService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees/{employeeId}/skills")
public class EmployeeSkillController {

    private final EmployeeSkillService service;

    public EmployeeSkillController(EmployeeSkillService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeSkillResponse> add(@PathVariable Long employeeId,
                                                     @Valid @RequestBody EmployeeSkillRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(employeeId, r));
    }

    @GetMapping
    public List<EmployeeSkillResponse> list(@PathVariable Long employeeId) {
        return service.findByEmployee(employeeId);
    }

    @PutMapping("/{skillId}")
    public EmployeeSkillResponse update(@PathVariable Long employeeId, @PathVariable Long skillId,
                                        @Valid @RequestBody EmployeeSkillUpdateRequest r) {
        return service.update(employeeId, skillId, r);
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> remove(@PathVariable Long employeeId, @PathVariable Long skillId) {
        service.remove(employeeId, skillId);
        return ResponseEntity.noContent().build();
    }
}