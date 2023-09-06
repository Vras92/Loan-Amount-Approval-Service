package com.mybank.contractapproval.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContractStatisticsDTO {

    private long sentContractsCount;
    private long approvedContractsCount;
    private long pendingContractsCount;
    private double sumLoanAmount;
    private double averageLoanAmount;
    private double maxLoanAmount;
    private double minLoanAmount;
    private List<ApproverStatistics> approverStatistics;

    public ContractStatisticsDTO(long pendingContractsCount, long approvedContractsCount,
                                 long sentContractsCount) {
        this.sentContractsCount = sentContractsCount;
        this.approvedContractsCount = approvedContractsCount;
        this.pendingContractsCount = pendingContractsCount;
    }

    public ContractStatisticsDTO(long pendingContractsCount, long approvedContractsCount,
                                 long sentContractsCount, double sumLoanAmount,
                                 double averageLoanAmount, double maxLoanAmount,
                                 double minLoanAmount, List<ApproverStatistics> approverStatistics) {
        this.sentContractsCount = sentContractsCount;
        this.approvedContractsCount = approvedContractsCount;
        this.pendingContractsCount = pendingContractsCount;
        this.sumLoanAmount = sumLoanAmount;
        this.averageLoanAmount = averageLoanAmount;
        this.maxLoanAmount = maxLoanAmount;
        this.minLoanAmount = minLoanAmount;
        this.approverStatistics = approverStatistics;
    }
}
