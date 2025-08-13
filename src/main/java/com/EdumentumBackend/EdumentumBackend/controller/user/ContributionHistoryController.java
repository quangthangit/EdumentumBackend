package com.EdumentumBackend.EdumentumBackend.controller.user;

import com.EdumentumBackend.EdumentumBackend.service.ContributionHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/contribution-history")
public class ContributionHistoryController {

    private final ContributionHistoryService contributionHistoryService;

    public ContributionHistoryController(ContributionHistoryService contributionHistoryService) {
        this.contributionHistoryService = contributionHistoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContributionHistory(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Get Group Detail successfully",
                "data", contributionHistoryService.getContributionHistory(id)
        ));
    }
}
