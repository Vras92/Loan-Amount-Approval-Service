package com.mybank.contractapproval.service;

import com.mybank.contractapproval.exceptions.ConflictException;
import com.mybank.contractapproval.exceptions.NotFoundException;
import com.mybank.contractapproval.model.Approver;
import com.mybank.contractapproval.model.LoanContractRequest;
import com.mybank.contractapproval.model.enums.ApprovalStatus;
import com.mybank.contractapproval.model.enums.ContractStatus;
import com.mybank.contractapproval.model.enums.LoanType;
import com.mybank.contractapproval.repository.LoanContractRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@EnableScheduling
@Setter
public class LoanApprovalService {

    @Autowired
    public LoanContractRepository repository;

    private static final String MSG = "Customer not found!";

    public List<LoanContractRequest> findAll(){
        log.info("Fetching all loan contracts.");
        return repository.loanContractRequestList;
    }

    public Optional<LoanContractRequest> findById (String customerId) {
        log.info("Fetching customer by ID: {}", customerId);
        return repository.loanContractRequestList
                .stream().filter(loanContractRequest -> loanContractRequest.customerId().equals(customerId)).findFirst();
    }

    public LoanContractRequest contractStatusUpdate(LoanContractRequest cRequest,
                                                           ContractStatus contractStatus) {
        return new LoanContractRequest(
                cRequest.customerId(),
                cRequest.loanAmount(),
                cRequest.approvers(),
                cRequest.loanType(),
                contractStatus,
                cRequest.createdDate(),
                checkSentToCustomerDate(cRequest)
        );
    }

    public LoanContractRequest updateStatus(LoanContractRequest cRequest,
                                                   ContractStatus contractStatus,
                                                   ApprovalStatus approvalStatus) {
        return new LoanContractRequest(
                cRequest.customerId(),
                cRequest.loanAmount(),
                approverStatusToPending(cRequest.approvers(), approvalStatus),
                cRequest.loanType(),
                contractStatus,
                LocalDateTime.now(),
                checkSentToCustomerDate(cRequest)
        );
    }

    public String checkSentToCustomerDate(LoanContractRequest cRequest) {
        if (areAllApprovalsCollected(cRequest) && hasSufficientApprovals(cRequest))
            return LocalDateTime.now().toString();

        return "Not approved yet";
    }

    public static List<Approver> approverStatusToPending(List<Approver> approvers,
                                                         ApprovalStatus status) {
        return approvers.stream()
                .map(approver -> new Approver(approver.username(), status))
                .toList();
    }

    public LoanContractRequest getUpdatedContractRequest(LoanContractRequest updatedRequest,
                                                              LoanContractRequest existingRequest,
                                                              int approverIndex) {
        var updatedApprovers = new ArrayList<>(existingRequest.approvers());
        updatedApprovers.set(approverIndex, updatedRequest.approvers().get(0));

        return new LoanContractRequest(
                existingRequest.customerId(),
                existingRequest.loanAmount(),
                updatedApprovers,
                existingRequest.loanType(),
                existingRequest.loanContractStatus(),
                existingRequest.createdDate(),
                existingRequest.sentToCustomerDate()
        );
    }

    public void makeDecision(LoanContractRequest updatedRequest) {
        var existingRequest = findById(updatedRequest.customerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG));

        if (findApproverIndex(existingRequest, updatedRequest) != -1) {
            var updatedContractRequest = getUpdatedContractRequest(
                    updatedRequest, existingRequest, findApproverIndex(existingRequest, updatedRequest));

            repository.replace(updatedContractRequest);

            } else {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Approver not found!");
        }
    }

    private int findApproverIndex(LoanContractRequest existingRequest, LoanContractRequest updatedRequest) {
        for (int i = 0; i < existingRequest.approvers().size(); i++) {
            if (existingRequest.approvers().get(i).username().equals(updatedRequest.approvers().get(0).username())) {

                return i;
            }
        }

        return -1;
    }

    private boolean areAllApprovalsCollected(LoanContractRequest request) {
        return request.approvers().stream()
                .allMatch(approver -> approver.status() == ApprovalStatus.APPROVED);
    }

    public boolean hasSufficientApprovals(LoanContractRequest request) {
        if (request.loanType() == LoanType.GENERAL) {

            return !request.approvers().isEmpty();

        } else if (request.loanType() == LoanType.MORTGAGE) {

            return request.approvers().size() >= 2;

        } else return false;
    }

    public void makeDecisionOnContract(LoanContractRequest updatedRequest) {
        if (areAllApprovalsCollected(updatedRequest) && hasSufficientApprovals(updatedRequest)) {

            repository.replace(contractStatusUpdate(updatedRequest, ContractStatus.SENT));
        }

        else repository.replace(contractStatusUpdate(updatedRequest, ContractStatus.PENDING));
    }

    public LoanContractRequest populateMissingFields(LoanContractRequest inputRequest) {
        var existingRequest = findById(inputRequest.customerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG));

        return new LoanContractRequest(
                inputRequest.customerId(),
                existingRequest.loanAmount(),
                existingRequest.approvers(),
                existingRequest.loanType(),
                existingRequest.loanContractStatus(),
                existingRequest.createdDate(),
                existingRequest.sentToCustomerDate()
        );
    }

    public static long countContractsByStatus(List<LoanContractRequest> contracts,
                                              ContractStatus status) {
        return contracts.stream()
                .filter(contract -> contract.loanContractStatus() == status)
                .count();
    }

    public void conflictException(LoanContractRequest cRequest) throws ConflictException {
        if (repository.statusContractPending(cRequest)) {
            throw new ConflictException("One pending contract is already active!");
        }
    }

    public void notFoundException(LoanContractRequest updatedRequest) throws NotFoundException {
        if (!repository.existsById(updatedRequest.customerId())) {
            throw new NotFoundException("Customer '" + updatedRequest.customerId() + "' not found");
        }
    }

    public void notFoundException(String id) throws NotFoundException {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Customer '" + id + "' not found");
        }
    }

    @CacheEvict(value = "data", allEntries = true)
    @Scheduled(fixedRateString = "${cache.update.time}", timeUnit = TimeUnit.MINUTES)
    public void emptyCache() {
        log.info("Emptying cache");
    }
}
