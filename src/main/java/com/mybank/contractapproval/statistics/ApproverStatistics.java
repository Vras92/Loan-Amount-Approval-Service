package com.mybank.contractapproval.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproverStatistics {

    private String username;
    private long numberOfApprovals;

    public ApproverStatistics(String username, long numberOfApprovals) {
        this.username = username;
        this.numberOfApprovals = numberOfApprovals;
    }
}