package com.civicpulse.backend.service;

import com.civicpulse.backend.dto.IssueRequest;
import com.civicpulse.backend.dto.IssueResponse;
import com.civicpulse.backend.dto.StatusUpdateRequest;
import com.civicpulse.backend.model.Issue;
import com.civicpulse.backend.model.IssueCategory;
import com.civicpulse.backend.model.IssueStatus;
import com.civicpulse.backend.model.StatusHistory;
import com.civicpulse.backend.model.User;
import com.civicpulse.backend.repository.IssueRepository;
import com.civicpulse.backend.repository.StatusHistoryRepository;
import com.civicpulse.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;

    public IssueService(IssueRepository issueRepository,
                        StatusHistoryRepository statusHistoryRepository,
                        UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public IssueResponse createIssue(IssueRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Issue issue = new Issue();
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setCategory(request.getCategory());
        issue.setLatitude(request.getLatitude());
        issue.setLongitude(request.getLongitude());
        issue.setImageUrl(request.getImageUrl());
        issue.setSubmittedBy(user);
        
        // Status defaults to OPEN as defined in Issue.java
        Issue savedIssue = issueRepository.save(issue);

        // Record initial status in history
        recordHistory(savedIssue, user, null, IssueStatus.OPEN, "Issue reported");

        return convertToResponse(savedIssue);
    }

    public List<IssueResponse> getAllIssues(IssueStatus status, IssueCategory category) {
        List<Issue> issues;

        if (status != null && category != null) {
            issues = issueRepository.findAllByCategoryAndStatus(category, status);
        } else if (status != null) {
            issues = issueRepository.findAllByStatus(status);
        } else {
            // Default sort: custom priority score via DB native query
            issues = issueRepository.findAllSortedByPriorityScore();
        }

        return issues.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public IssueResponse getIssueById(UUID id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        IssueResponse response = convertToResponse(issue);
        response.setHistory(statusHistoryRepository.findAllByIssueIdOrderByChangedAtDesc(id));
        return response;
    }

    @Transactional
    public IssueResponse updateIssueStatus(UUID id, StatusUpdateRequest request, String adminEmail) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        IssueStatus oldStatus = issue.getStatus();
        IssueStatus newStatus = request.getStatus();

        if (oldStatus == newStatus) {
            return convertToResponse(issue); // No change needed
        }

        issue.setStatus(newStatus);
        Issue savedIssue = issueRepository.save(issue);

        // Audit log the status change
        recordHistory(savedIssue, admin, oldStatus, newStatus, request.getNote());

        return convertToResponse(savedIssue);
    }

    @Transactional
    public void deleteIssue(UUID id) {
        if (!issueRepository.existsById(id)) {
            throw new RuntimeException("Issue not found");
        }
        // Because of JPA constraints, we might want to cascade or delete related history/upvotes first
        issueRepository.deleteById(id);
    }

    // --- Helper Methods ---

    private void recordHistory(Issue issue, User changedBy, IssueStatus from, IssueStatus to, String note) {
        StatusHistory history = new StatusHistory();
        history.setIssue(issue);
        history.setChangedBy(changedBy);
        history.setFromStatus(from);
        history.setToStatus(to);
        history.setNote(note);
        statusHistoryRepository.save(history);
    }

    private IssueResponse convertToResponse(Issue issue) {
        return new IssueResponse(issue, computePriorityScore(issue));
    }

    /**
     * Replicates the database priority score logic in Java for single objects.
     * priority_score = (upvotes x 2) + (days_since_reported x 0.5) + category_weight
     */
    private double computePriorityScore(Issue issue) {
        long daysSince = ChronoUnit.DAYS.between(issue.getCreatedAt(), LocalDateTime.now());
        int catWeight = switch (issue.getCategory()) {
            case STREETLIGHT -> 5;
            case POTHOLE -> 4;
            case GARBAGE -> 3;
            default -> 1; // OTHER
        };
        
        return (issue.getUpvoteCount() * 2.0) + (daysSince * 0.5) + catWeight;
    }
}
