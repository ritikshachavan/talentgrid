package com.talentgrid.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentgrid.dto.SkillCategoryRequest;
import com.talentgrid.dto.SkillCategoryResponse;
import com.talentgrid.service.SkillCategoryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/skill-categories")
public class SkillCategoryController {

    private final SkillCategoryService service;

    public SkillCategoryController(SkillCategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SkillCategoryResponse> create(@Valid @RequestBody SkillCategoryRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @GetMapping
    public List<SkillCategoryResponse> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public SkillCategoryResponse one(@PathVariable Long id) { return service.findById(id); }

    @PutMapping("/{id}")
    public SkillCategoryResponse update(@PathVariable Long id, @Valid @RequestBody SkillCategoryRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}