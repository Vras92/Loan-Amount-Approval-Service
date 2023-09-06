package com.mybank.contractapproval.service;

import com.mybank.contractapproval.exceptions.ConflictException;
import com.mybank.contractapproval.exceptions.NotFoundException;
import com.mybank.contractapproval.model.Approver;
import com.mybank.contractapproval.model.LoanContractRequest;
import com.mybank.contractapproval.model.enums.ApprovalStatus;
import com.mybank.contractapproval.model.enums.ContractStatus;
import com.mybank.contractapproval.model.enums.LoanType;
import com.mybank.contractapproval.model.enums.Managers;
import com.mybank.contractapproval.repository.LoanContractRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class LoanApprovalServiceTest {

    @InjectMocks
    private LoanApprovalService loanApprovalService;

    @Mock
    private LoanContractRepository repository;

    @Test
    void findAll() {
        List<LoanContractRequest> contractRequests = List.of(
                new LoanContractRequest(
                        "1-123-12345G",
                        200000.0,
                        List.of(
                                new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                                new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                        ),
                        LoanType.GENERAL,
                        ContractStatus.PENDING,
                        LocalDateTime.now(),
                        "Not approved yet"
                ),
                new LoanContractRequest(
                        "2-123-12345G",
                        250000.0,
                        List.of(
                                new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                                new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                        ),
                        LoanType.MORTGAGE,
                        ContractStatus.SENT,
                        LocalDateTime.now(),
                        LocalDateTime.now().toString()
                )
        );

        when(repository.findAllContracts()).thenReturn(contractRequests);

        List<LoanContractRequest> foundContracts = repository.findAllContracts();

        assertEquals(contractRequests.size(), foundContracts.size());
        for (int i = 0; i < contractRequests.size(); i++) {
            assertEquals(contractRequests.get(i).customerId(), foundContracts.get(i).customerId());
            assertEquals(contractRequests.get(i).loanAmount(), foundContracts.get(i).loanAmount());
        }
    }

    @Test
    void contractStatusUpdate() {
        LoanContractRequest contractRequestPending = new LoanContractRequest(
                "9-123-12345G",
                150000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        LoanContractRequest updatedContract = loanApprovalService.contractStatusUpdate(
                contractRequestPending,
                ContractStatus.SENT
        );

        assertEquals(ContractStatus.SENT, updatedContract.loanContractStatus());
        assertEquals(contractRequestPending.customerId(), updatedContract.customerId());
        assertEquals(contractRequestPending.loanAmount(), updatedContract.loanAmount());
    }

    @Test
    void updateStatus() {
        LoanContractRequest contractRequestPending = new LoanContractRequest(
                "8-123-12345G",
                120000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        LoanContractRequest updatedContract = loanApprovalService.updateStatus(
                contractRequestPending,
                ContractStatus.SENT,
                ApprovalStatus.APPROVED
        );

        assertEquals(ContractStatus.SENT, updatedContract.loanContractStatus());
        assertEquals(ApprovalStatus.APPROVED, updatedContract.approvers().get(0).status());
        assertEquals(contractRequestPending.customerId(), updatedContract.customerId());
        assertEquals(contractRequestPending.loanAmount(), updatedContract.loanAmount());
    }

    @Test
    void checkSentToCustomerDate() {
        LoanContractRequest contractRequestApproved = new LoanContractRequest(
                "6-123-12345G",
                100000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.MORTGAGE,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        LoanContractRequest contractRequestPending = new LoanContractRequest(
                "7-123-12345G",
                100000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.MORTGAGE,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        String sentToCustomerDateApproved = loanApprovalService.checkSentToCustomerDate(contractRequestApproved);
        String sentToCustomerDatePending = loanApprovalService.checkSentToCustomerDate(contractRequestPending);

        assertEquals(LocalDateTime.now().toString(), sentToCustomerDateApproved);
        assertEquals("Not approved yet", sentToCustomerDatePending);
    }

    @Test
    void approverStatusToPending() {
        List<Approver> approvers = List.of(
                new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                new Approver(Managers.P998ABC, ApprovalStatus.APPROVED),
                new Approver(Managers.P998LOL, ApprovalStatus.PENDING)
        );

        List<Approver> updatedApprovers = loanApprovalService.approverStatusToPending(approvers, ApprovalStatus.PENDING);

        assertTrue(updatedApprovers.stream().allMatch(approver -> approver.status() == ApprovalStatus.PENDING));
        assertEquals(approvers.size(), updatedApprovers.size());
    }

    @Test
    void getUpdatedContractRequest() {
        LoanContractRequest existingContract = new LoanContractRequest(
                "5-123-12345G",
                90000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.MORTGAGE,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        LoanContractRequest updatedRequest = new LoanContractRequest(
                "5-123-12345G",
                90000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.MORTGAGE,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        when(repository.existsById(existingContract.customerId())).thenReturn(true);

        LoanContractRequest updatedContract = loanApprovalService.getUpdatedContractRequest(updatedRequest, existingContract, 0);

        assertEquals(ApprovalStatus.APPROVED, updatedContract.approvers().get(0).status());
        assertEquals(existingContract.customerId(), updatedContract.customerId());
        assertEquals(existingContract.loanAmount(), updatedContract.loanAmount());

        assertEquals(ContractStatus.PENDING, updatedContract.loanContractStatus());
    }

    @Test
    void hasSufficientApprovals() {
        LoanContractRequest contractWithGeneralLoan = new LoanContractRequest(
                "1-123-12345G",
                50000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );
        LoanContractRequest contractWithMortgageLoan = new LoanContractRequest(
                "2-123-12345G",
                60000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING)
                ),
                LoanType.MORTGAGE,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        boolean hasSufficientApprovalsGeneral = loanApprovalService.hasSufficientApprovals(contractWithGeneralLoan);
        boolean hasSufficientApprovalsMortgage = loanApprovalService.hasSufficientApprovals(contractWithMortgageLoan);

        assertTrue(hasSufficientApprovalsGeneral);
        assertFalse(hasSufficientApprovalsMortgage);
    }

    @Test
    void populateMissingFields() {
        var repository = new LoanContractRepository();

        LoanContractRequest existingContract = new LoanContractRequest(
                "1-123-12345G",
                60000.0,
                List.of(
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        LoanApprovalService loanApprovalService = new LoanApprovalService();
        loanApprovalService.setRepository(repository);

        repository.loanContractRequestList.add(existingContract);

        LoanContractRequest inputRequest = new LoanContractRequest(
                "1-123-12345G",
                60000.0,
                List.of(
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        LoanContractRequest updatedContract = loanApprovalService.populateMissingFields(inputRequest);

        assertEquals(existingContract.customerId(), updatedContract.customerId());
        assertEquals(inputRequest.approvers(), updatedContract.approvers());
        assertEquals(inputRequest.loanType(), updatedContract.loanType());
        assertEquals(inputRequest.loanContractStatus(), updatedContract.loanContractStatus());
        assertEquals(inputRequest.createdDate(), updatedContract.createdDate());
        assertEquals(inputRequest.sentToCustomerDate(), updatedContract.sentToCustomerDate());
    }

    @Test
    void countContractsByStatus() {
        List<LoanContractRequest> contracts = new ArrayList<>();

        LoanContractRequest contract1 = new LoanContractRequest(
                "1-123-12345G",
                50000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        LoanContractRequest contract2 = new LoanContractRequest(
                "2-123-12345G",
                60000.0,
                List.of(
                        new Approver(Managers.P998LOL, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                LocalDateTime.now().toString()
        );

        contracts.add(contract1);
        contracts.add(contract2);

        when(repository.findAllContracts()).thenReturn(contracts);

        var pendingContracts = loanApprovalService.countContractsByStatus(contracts,
                ContractStatus.PENDING);
        var sentContracts = loanApprovalService.countContractsByStatus(contracts,
                ContractStatus.SENT);

        assertEquals(1, pendingContracts);
        assertEquals(1, sentContracts);
    }

    @Test
    void conflictException() {
        LoanContractRequest existingContract = new LoanContractRequest(
                "1-123-12345G",
                50000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        when(repository.statusContractPending(existingContract)).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            loanApprovalService.conflictException(existingContract);
        });
    }

    @Test
    void notFoundException() {
        String nonExistentCustomerId = "9-123-12345G";

        assertThrows(NotFoundException.class, () -> {
            loanApprovalService.notFoundException(nonExistentCustomerId);
        });
    }

    @Test
    void testNotFoundException() {
        String nonExistentCustomerId = "9-123-12345G";

        assertThrows(NotFoundException.class, () -> {
            loanApprovalService.notFoundException(nonExistentCustomerId);
        });
    }
}