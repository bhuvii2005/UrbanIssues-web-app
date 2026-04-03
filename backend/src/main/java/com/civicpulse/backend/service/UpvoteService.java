package com.civicpulse.backend.service;

import com.civicpulse.backend.model.Issue;
import com.civicpulse.backend.model.Upvote;
import com.civicpulse.backend.model.UpvoteId;
import com.civicpulse.backend.model.User;
import com.civicpulse.backend.repository.IssueRepository;
import com.civicpulse.backend.repository.UpvoteRepository;
import com.civicpulse.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpvoteService {

    private final UpvoteRepository upvoteRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public UpvoteService(UpvoteRepository upvoteRepository, 
                         IssueRepository issueRepository, 
                         UserRepository userRepository) {
        this.upvoteRepository = upvoteRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addUpvote(UUID issueId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (upvoteRepository.existsByUserIdAndIssueId(user.getId(), issueId)) {
            throw new RuntimeException("Duplicated upvote"); // Will be mapped to 409 Conflict in controller
        }

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        // Save upvote mapping
        Upvote upvote = new Upvote(user.getId(), issueId);
        upvoteRepository.save(upvote);

        // Increment count transactionally
        issue.setUpvoteCount(issue.getUpvoteCount() + 1);
        issueRepository.save(issue);
    }
}
