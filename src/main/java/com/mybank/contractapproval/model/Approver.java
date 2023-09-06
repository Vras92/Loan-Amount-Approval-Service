package com.mybank.contractapproval.model;

import com.mybank.contractapproval.model.enums.ApprovalStatus;
import com.mybank.contractapproval.model.enums.Managers;
import jakarta.validation.constraints.NotEmpty;

public record Approver(
        Managers username,
        @NotEmpty(message = "Status is incorrect. Required status: REJECTED, PENDING or APPROVED")
        ApprovalStatus status
) {
}
