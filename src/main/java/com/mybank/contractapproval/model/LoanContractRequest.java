package com.mybank.contractapproval.model;

import com.mybank.contractapproval.model.enums.ContractStatus;
import com.mybank.contractapproval.model.enums.LoanType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record LoanContractRequest(
    @Pattern(regexp = "[A-Za-z0-9]{1}-[A-Za-z0-9]{3}-[A-Za-z0-9]{6}", message =
                "ID must follow the pattern X-XXX-XXXXXX")
    String customerId,
    @Positive(message = "Loan amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Loan amount must have a maximum of 2 decimals")
    Double loanAmount,
    @Size(max = 5, message = "There can be a maximum of 5 approvers")
    List<Approver> approvers,
    LoanType loanType,
    ContractStatus loanContractStatus,
    LocalDateTime createdDate,
    String sentToCustomerDate
) {
}