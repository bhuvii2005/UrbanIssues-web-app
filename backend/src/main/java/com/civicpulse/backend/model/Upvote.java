package com.civicpulse.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "upvotes")
@IdClass(UpvoteId.class)
public class Upvote {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "issue_id")
    private UUID issueId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors

    public Upvote() {}

    public Upvote(UUID userId, UUID issueId) {
        this.userId = userId;
        this.issueId = issueId;
    }

    // Getters and Setters

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getIssueId() { return issueId; }
    public void setIssueId(UUID issueId) { this.issueId = issueId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
