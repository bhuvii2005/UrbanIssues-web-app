package com.civicpulse.backend.dto;

import com.civicpulse.backend.model.Issue;
import com.civicpulse.backend.model.IssueCategory;
import com.civicpulse.backend.model.IssueStatus;
import com.civicpulse.backend.model.StatusHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class IssueResponse {
    
    private UUID id;
    private String title;
    private String description;
    private IssueCategory category;
    private IssueStatus status;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private Integer upvoteCount;
    private String submittedByName;
    private LocalDateTime createdAt;
    
    // Computed dynamically on the server:
    private double priorityScore;

    // Optional list of history (e.g. for full detailed view)
    private List<StatusHistory> history;

    public IssueResponse() {}

    public IssueResponse(Issue issue, double priorityScore) {
        this.id = issue.getId();
        this.title = issue.getTitle();
        this.description = issue.getDescription();
        this.category = issue.getCategory();
        this.status = issue.getStatus();
        this.latitude = issue.getLatitude();
        this.longitude = issue.getLongitude();
        this.imageUrl = issue.getImageUrl();
        this.upvoteCount = issue.getUpvoteCount();
        this.submittedByName = issue.getSubmittedBy().getName();
        this.createdAt = issue.getCreatedAt();
        this.priorityScore = priorityScore;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public IssueCategory getCategory() { return category; }
    public void setCategory(IssueCategory category) { this.category = category; }
    public IssueStatus getStatus() { return status; }
    public void setStatus(IssueStatus status) { this.status = status; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getUpvoteCount() { return upvoteCount; }
    public void setUpvoteCount(Integer upvoteCount) { this.upvoteCount = upvoteCount; }
    public String getSubmittedByName() { return submittedByName; }
    public void setSubmittedByName(String submittedByName) { this.submittedByName = submittedByName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(double priorityScore) { this.priorityScore = priorityScore; }
    public List<StatusHistory> getHistory() { return history; }
    public void setHistory(List<StatusHistory> history) { this.history = history; }
}
