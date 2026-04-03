package com.civicpulse.backend.controller;

import com.civicpulse.backend.dto.IssueRequest;
import com.civicpulse.backend.dto.IssueResponse;
import com.civicpulse.backend.dto.StatusUpdateRequest;
import com.civicpulse.backend.model.IssueCategory;
import com.civicpulse.backend.model.IssueStatus;
import com.civicpulse.backend.service.IssueService;
import com.civicpulse.backend.service.UpvoteService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*") // For local dev
public class IssueController {

    private final IssueService issueService;
    private final UpvoteService upvoteService;
    
    // Simple in-memory rate limiting map for user emails -> buckets
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    public IssueController(IssueService issueService, UpvoteService upvoteService) {
        this.issueService = issueService;
        this.upvoteService = upvoteService;
    }

    // Rate limit: 5 requests per 1 hour
    private Bucket resolveBucket(String email) {
        return userBuckets.computeIfAbsent(email, key -> {
            Refill refill = Refill.intervally(5, Duration.ofHours(1));
            Bandwidth limit = Bandwidth.classic(5, refill);
            return Bucket4j.builder().addLimit(limit).build();
        });
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAll(
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) IssueCategory category) {
        return ResponseEntity.ok(issueService.getAllIssues(status, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(issueService.getIssueById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> createIssue(@RequestBody @Valid IssueRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Bucket bucket = resolveBucket(email);
        if (bucket.tryConsume(1)) {
            try {
                IssueResponse response = issueService.createIssue(request, email);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Rate limit exceeded. Max 5 reports per hour."));
        }
    }

    @PostMapping("/{id}/upvote")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> upvoteIssue(@PathVariable UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            upvoteService.addUpvote(id, auth.getName());
            return ResponseEntity.ok(Map.of("message", "Upvote recorded"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Duplicated upvote")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "You have already upvoted this issue"));
            }
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestBody @Valid StatusUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            IssueResponse response = issueService.updateIssueStatus(id, request, auth.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteIssue(@PathVariable UUID id) {
        try {
            issueService.deleteIssue(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
