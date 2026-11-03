package com.talentgrid.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentgrid.dto.AllocationRequest;
import com.talentgrid.dto.AllocationResponse;
import com.talentgrid.service.AllocationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/allocations")
public class AllocationController {

    private final AllocationService service;

    public AllocationController(AllocationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AllocationResponse> create(@Valid @RequestBody AllocationRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @GetMapping
    public List<AllocationResponse> all(@RequestParam(required = false) Long employeeId,
                                        @RequestParam(required = false) Long projectId) {
        return service.findAll(employeeId, projectId);
    }

    @GetMapping("/{id}")
    public AllocationResponse one(@PathVariable Long id) { return service.findById(id); }

    @PutMapping("/{id}")
    public AllocationResponse update(@PathVariable Long id, @Valid @RequestBody AllocationRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> release(@PathVariable Long id) {
        service.release(id);
        return ResponseEntity.noContent().build();
    }
}