package com.civicpulse.backend.repository;

import com.civicpulse.backend.model.Upvote;
import com.civicpulse.backend.model.UpvoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UpvoteRepository extends JpaRepository<Upvote, UpvoteId> {
    boolean existsByUserIdAndIssueId(UUID userId, UUID issueId);
}
