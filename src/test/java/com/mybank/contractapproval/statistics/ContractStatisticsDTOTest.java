package com.mybank.contractapproval.statistics;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContractStatisticsDTOTest {

    @Test
    void getSentContractsCount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15);
        assertEquals(15, dto.getSentContractsCount());
    }

    @Test
    void getApprovedContractsCount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15);
        assertEquals(10, dto.getApprovedContractsCount());
    }

    @Test
    void getPendingContractsCount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15);
        assertEquals(5, dto.getPendingContractsCount());
    }

    @Test
    void getSumLoanAmount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        assertEquals(50000.0, dto.getSumLoanAmount());
    }

    @Test
    void getAverageLoanAmount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        assertEquals(10000.0, dto.getAverageLoanAmount());
    }

    @Test
    void getMaxLoanAmount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        assertEquals(20000.0, dto.getMaxLoanAmount());
    }

    @Test
    void getMinLoanAmount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        assertEquals(5000.0, dto.getMinLoanAmount());
    }

    @Test
    void getApproverStatistics() {
        ApproverStatistics approverStatistics = new ApproverStatistics("John", 5);
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of(approverStatistics));
        assertEquals(1, dto.getApproverStatistics().size());
    }

    @Test
    void setSentContractsCount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15);
        dto.setSentContractsCount(20);
        assertEquals(20, dto.getSentContractsCount());
    }

    @Test
    void setApprovedContractsCount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15);
        dto.setApprovedContractsCount(25);
        assertEquals(25, dto.getApprovedContractsCount());
    }

    @Test
    void setPendingContractsCount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15);
        dto.setPendingContractsCount(30);
        assertEquals(30, dto.getPendingContractsCount());
    }

    @Test
    void setSumLoanAmount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        dto.setSumLoanAmount(75000.0);
        assertEquals(75000.0, dto.getSumLoanAmount());
    }

    @Test
    void setAverageLoanAmount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        dto.setAverageLoanAmount(12500.0);
        assertEquals(12500.0, dto.getAverageLoanAmount());
    }

    @Test
    void setMaxLoanAmount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        dto.setMaxLoanAmount(30000.0);
        assertEquals(30000.0, dto.getMaxLoanAmount());
    }

    @Test
    void setMinLoanAmount() {
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        dto.setMinLoanAmount(2500.0);
        assertEquals(2500.0, dto.getMinLoanAmount());
    }

    @Test
    void setApproverStatistics() {
        ApproverStatistics approverStatistics = new ApproverStatistics("Alice", 8);
        ContractStatisticsDTO dto = new ContractStatisticsDTO(5, 10, 15, 50000.0, 10000.0, 20000.0, 5000.0, List.of());
        dto.setApproverStatistics(List.of(approverStatistics));
        assertEquals(1, dto.getApproverStatistics().size());
    }
}