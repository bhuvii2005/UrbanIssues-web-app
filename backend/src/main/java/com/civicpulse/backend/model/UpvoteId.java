package com.civicpulse.backend.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UpvoteId implements Serializable {
    private UUID userId;
    private UUID issueId;

    public UpvoteId() {}

    public UpvoteId(UUID userId, UUID issueId) {
        this.userId = userId;
        this.issueId = issueId;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getIssueId() { return issueId; }
    public void setIssueId(UUID issueId) { this.issueId = issueId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpvoteId upvoteId = (UpvoteId) o;
        return Objects.equals(userId, upvoteId.userId) &&
               Objects.equals(issueId, upvoteId.issueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, issueId);
    }
}
