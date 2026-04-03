package com.civicpulse.backend.repository;

import com.civicpulse.backend.model.Issue;
import com.civicpulse.backend.model.IssueCategory;
import com.civicpulse.backend.model.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IssueRepository extends JpaRepository<Issue, UUID> {
    
    List<Issue> findAllByStatus(IssueStatus status);
    
    List<Issue> findAllByCategoryAndStatus(IssueCategory category, IssueStatus status);

    // Custom query to sort by the requested formula:
    // (upvotes * 2) + (days_since * 0.5) + category_weight
    // Using standard SQL functions for PostgreSQL syntax compatible with Postgres 15
    @Query(value = "SELECT * FROM issues ORDER BY " +
           "(upvote_count * 2) + " +
           "(DATE_PART('day', NOW() - created_at) * 0.5) + " +
           "(CASE category " +
           "  WHEN 'STREETLIGHT' THEN 5 " +
           "  WHEN 'POTHOLE' THEN 4 " +
           "  WHEN 'GARBAGE' THEN 3 " +
           "  ELSE 1 END) DESC", nativeQuery = true)
    List<Issue> findAllSortedByPriorityScore();
}
