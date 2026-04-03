package com.civicpulse.backend.dto;

import com.civicpulse.backend.model.IssueStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status cannot be null")
    private IssueStatus status;

    private String note;

    public IssueStatus getStatus() { return status; }
    public void setStatus(IssueStatus status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
