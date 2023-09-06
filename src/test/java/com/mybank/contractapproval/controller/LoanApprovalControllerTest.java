package com.mybank.contractapproval.controller;

import com.mybank.contractapproval.exceptions.ConflictException;
import com.mybank.contractapproval.exceptions.NotFoundException;
import com.mybank.contractapproval.model.Approver;
import com.mybank.contractapproval.model.LoanContractRequest;
import com.mybank.contractapproval.model.enums.ApprovalStatus;
import com.mybank.contractapproval.model.enums.ContractStatus;
import com.mybank.contractapproval.model.enums.LoanType;
import com.mybank.contractapproval.model.enums.Managers;
import com.mybank.contractapproval.repository.LoanContractRepository;
import com.mybank.contractapproval.service.LoanApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LoanApprovalControllerTest {

    @InjectMocks
    private LoanApprovalController loanApprovalController;

    @Mock
    private LoanContractRepository repository;

    @Mock
    private LoanApprovalService loanApprovalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll() {
        var contract1 = new LoanContractRequest(
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

        var contract2 = new LoanContractRequest(
                "2-123-12345G",
                60000.0,
                List.of(
                        new Approver(Managers.P998LOL, ApprovalStatus.PENDING),
                        new Approver(Managers.P998ART, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        List<LoanContractRequest> contractList = Arrays.asList(contract1, contract2);

        when(loanApprovalService.findAll()).thenReturn(contractList);

        List<LoanContractRequest> responseList = loanApprovalController.getAll();

        assertEquals(2, responseList.size());

        assertEquals(contract1.customerId(), responseList.get(0).customerId());
        assertEquals(contract2.customerId(), responseList.get(1).customerId());
    }

    @Test
    void getById() {
        var contract = new LoanContractRequest(
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

        when(loanApprovalService.findById(contract.customerId())).thenReturn(Optional.of(contract));

        LoanContractRequest responseEntity = loanApprovalController.getById(contract.customerId());

        assertEquals(contract, responseEntity);
    }

    @Test
    void create() throws ConflictException {
        var contractRequest = new LoanContractRequest(
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

        when(loanApprovalService.updateStatus(any(LoanContractRequest.class), eq(ContractStatus.PENDING), eq(ApprovalStatus.PENDING)))
                .thenReturn(contractRequest);

        loanApprovalController.create(contractRequest);

        verify(repository).save(ArgumentMatchers.any(LoanContractRequest.class));
    }

    @Test
    void decision() throws NotFoundException {
        var updatedRequest = new LoanContractRequest(
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

        doNothing().when(loanApprovalService).notFoundException(updatedRequest);
        doNothing().when(loanApprovalService).makeDecision(updatedRequest);
        when(loanApprovalService.populateMissingFields(updatedRequest)).thenReturn(updatedRequest);

        loanApprovalController.decision(updatedRequest);

        verify(loanApprovalService).notFoundException(updatedRequest);
        verify(loanApprovalService).makeDecision(updatedRequest);
        verify(loanApprovalService).populateMissingFields(updatedRequest);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete() throws NotFoundException {
        String customerId = "1-123-12345G";

        doNothing().when(loanApprovalService).notFoundException(customerId);

        loanApprovalController.delete(customerId);

        verify(loanApprovalService).notFoundException(customerId);

        verify(repository).deleteById(customerId);
    }
}
