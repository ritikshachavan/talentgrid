package com.talentgrid.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentgrid.dto.RequiredSkillRequest;
import com.talentgrid.dto.RequiredSkillResponse;
import com.talentgrid.dto.RequiredSkillUpdateRequest;
import com.talentgrid.service.ProjectRequiredSkillService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects/{projectId}/required-skills")
public class ProjectRequiredSkillController {

    private final ProjectRequiredSkillService service;

    public ProjectRequiredSkillController(ProjectRequiredSkillService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RequiredSkillResponse> add(@PathVariable Long projectId,
                                                     @Valid @RequestBody RequiredSkillRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(projectId, r));
    }

    @GetMapping
    public List<RequiredSkillResponse> list(@PathVariable Long projectId) {
        return service.findByProject(projectId);
    }

    @PutMapping("/{skillId}")
    public RequiredSkillResponse update(@PathVariable Long projectId, @PathVariable Long skillId,
                                        @Valid @RequestBody RequiredSkillUpdateRequest r) {
        return service.update(projectId, skillId, r);
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> remove(@PathVariable Long projectId, @PathVariable Long skillId) {
        service.remove(projectId, skillId);
        return ResponseEntity.noContent().build();
    }
}