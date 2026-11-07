package com.talentgrid.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.talentgrid.dto.RecommendationResponse;
import com.talentgrid.service.MatchingService;

@RestController
@RequestMapping("/api/projects")
public class MatchingController {

    private final MatchingService service;

    public MatchingController(MatchingService service) {
        this.service = service;
    }

    @GetMapping("/{projectId}/recommendations")
    public RecommendationResponse recommend(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "5") int top,
            @RequestParam(defaultValue = "WEIGHTED") String strategy) {
        return service.recommend(projectId, top, strategy);
    }
}