package com.talentgrid.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentgrid.dto.DesignationRequest;
import com.talentgrid.dto.DesignationResponse;
import com.talentgrid.service.DesignationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/designations")
public class DesignationController {

    private final DesignationService service;

    public DesignationController(DesignationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DesignationResponse> create(@Valid @RequestBody DesignationRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @GetMapping
    public List<DesignationResponse> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public DesignationResponse one(@PathVariable Long id) { return service.findById(id); }

    @PutMapping("/{id}")
    public DesignationResponse update(@PathVariable Long id, @Valid @RequestBody DesignationRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}