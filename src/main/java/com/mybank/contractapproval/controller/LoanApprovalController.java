package com.mybank.contractapproval.controller;

import com.mybank.contractapproval.exceptions.ConflictException;
import com.mybank.contractapproval.exceptions.NotFoundException;
import com.mybank.contractapproval.model.LoanContractRequest;
import com.mybank.contractapproval.model.enums.ApprovalStatus;
import com.mybank.contractapproval.model.enums.ContractStatus;
import com.mybank.contractapproval.repository.LoanContractRepository;
import com.mybank.contractapproval.service.LoanApprovalService;
import com.mybank.contractapproval.statistics.ContractStatisticsDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@AllArgsConstructor
public class LoanApprovalController {

    @Autowired
    private final LoanContractRepository repository;
    private final LoanApprovalService loanApprovalService;

    @Cacheable(value = "data")
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<LoanContractRequest> getAll() {
        return  loanApprovalService.findAll();
    }

    @Cacheable(value = "data", key = "#customerId")
    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public LoanContractRequest getById(@PathVariable String customerId) {
        return loanApprovalService.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer '" + customerId + "' not found"));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody LoanContractRequest cRequest) throws ConflictException {
        loanApprovalService.conflictException(cRequest);

        var loanApprovalService = new LoanApprovalService();
        var updatedContractRequest = loanApprovalService.updateStatus(cRequest,
                ContractStatus.PENDING, ApprovalStatus.PENDING);

        repository.save(updatedContractRequest);
    }

    @PutMapping("/decision")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void decision(@Valid @RequestBody LoanContractRequest updatedRequest)
            throws NotFoundException {
        loanApprovalService.notFoundException(updatedRequest);

        loanApprovalService.makeDecision(updatedRequest);
        loanApprovalService.makeDecisionOnContract(loanApprovalService.populateMissingFields(updatedRequest));
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable String customerId) throws NotFoundException {
        loanApprovalService.notFoundException(customerId);

        repository.deleteById(customerId);
    }

    @Cacheable(value = "data")
    @GetMapping("/statistics")
    public ResponseEntity<ContractStatisticsDTO> getContractStatistics(@RequestParam(defaultValue = "$" +
            "{statistics.sent.contracts.minutes.default}") int minutes) {
        var contractsInLastMinutes =
                repository.findSentContracts(minutes);
        var contracts = repository.findAllContracts();

        return ResponseEntity.ok(new ContractStatisticsDTO(
                LoanApprovalService.countContractsByStatus(contracts, ContractStatus.PENDING),
                LoanApprovalService.countContractsByStatus(contracts, ContractStatus.SENT),
                LoanApprovalService.countContractsByStatus(contractsInLastMinutes, ContractStatus.SENT),
                repository.calculateSumOfLoanAmounts(), repository.calculateAverageLoanAmount(),
                repository.findMaxLoanAmount(), repository.findMinLoanAmount(),
                repository.calculateApproverStatistics()));
    }
}
