package com.mybank.contractapproval.repository;

import com.mybank.contractapproval.model.Approver;
import com.mybank.contractapproval.model.LoanContractRequest;
import com.mybank.contractapproval.model.enums.ApprovalStatus;
import com.mybank.contractapproval.model.enums.ContractStatus;
import com.mybank.contractapproval.statistics.ApproverStatistics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@AllArgsConstructor
@Slf4j
public class LoanContractRepository {

    public final List<LoanContractRequest> loanContractRequestList = new ArrayList<>();

    public void save (LoanContractRequest cRequest){
        log.info("Contract has been created.");
        loanContractRequestList.add(cRequest);
    }

    public void replace (LoanContractRequest cRequest){
        loanContractRequestList.removeIf(loanContractRequest -> loanContractRequest.customerId
         ().equals(cRequest.customerId()));
        loanContractRequestList.add(cRequest);
        log.info("Contract has been updated.");
    }

    public boolean existsById(String id) {
        return loanContractRequestList
                .stream().filter(loanContractRequest -> loanContractRequest.customerId().equals(id)).count() == 1;
    }

    public void deleteById(String id) {
        log.info(id + " has been deleted.");
        loanContractRequestList.removeIf(loanContractRequest -> loanContractRequest.customerId().equals(id));
    }

    public boolean statusContractPending(LoanContractRequest cRequest) {
        return loanContractRequestList.stream()
                .filter(loanContractRequest -> loanContractRequest.customerId().equals(cRequest.customerId()))
                .anyMatch(loanContractRequest -> loanContractRequest.loanContractStatus() == ContractStatus.PENDING);
    }

    public List<LoanContractRequest> findSentContracts(int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusMinutes(minutes);

        return loanContractRequestList.stream()
                .filter(contract -> contract.createdDate().isAfter(startTime))
                .filter(contract -> contract.loanContractStatus() == ContractStatus.SENT)
                .toList();
    }

    public List<LoanContractRequest> findAllContracts() {
        return loanContractRequestList.stream()
                .toList();
    }

    public double calculateSumOfLoanAmounts() {
        double sum = loanContractRequestList.stream()
                .mapToDouble(LoanContractRequest::loanAmount)
                .sum();

        DecimalFormat df = new DecimalFormat("#.##");

        return Double.parseDouble(df.format(sum));
    }

    public double calculateAverageLoanAmount() {
        double average = loanContractRequestList.stream()
                .mapToDouble(LoanContractRequest::loanAmount)
                .average()
                .orElse(0.0);

        DecimalFormat df = new DecimalFormat("#.##");

        return Double.parseDouble(df.format(average));
    }

    public double findMaxLoanAmount() {
        return loanContractRequestList.stream()
                .mapToDouble(LoanContractRequest::loanAmount)
                .max()
                .orElse(0.0);
    }

    public double findMinLoanAmount() {
        return loanContractRequestList.stream()
                .mapToDouble(LoanContractRequest::loanAmount)
                .min()
                .orElse(0.0);
    }

    public List<ApproverStatistics> calculateApproverStatistics() {
        Map<String, Long> counter = new HashMap<>();

        for (LoanContractRequest contract : loanContractRequestList) {
            for (Approver approver : contract.approvers()) {
                if (approver.status() == ApprovalStatus.APPROVED) {
                    String username = approver.username().toString();
                    counter.put(username, counter.getOrDefault(username, 0L) + 1);
                }
            }
        }

        List<ApproverStatistics> approverStatistics = new ArrayList<>();
        for (Map.Entry<String, Long> entry : counter.entrySet()) {
            approverStatistics.add(new ApproverStatistics(entry.getKey(), entry.getValue()));
        }

        approverStatistics.sort(Comparator.comparing(ApproverStatistics::getNumberOfApprovals).reversed());

        return approverStatistics;
    }
}
