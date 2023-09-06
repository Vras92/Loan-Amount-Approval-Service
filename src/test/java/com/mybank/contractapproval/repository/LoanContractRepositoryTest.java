package com.mybank.contractapproval.repository;

import com.mybank.contractapproval.model.Approver;
import com.mybank.contractapproval.model.LoanContractRequest;
import com.mybank.contractapproval.model.enums.ApprovalStatus;
import com.mybank.contractapproval.model.enums.ContractStatus;
import com.mybank.contractapproval.model.enums.LoanType;
import com.mybank.contractapproval.model.enums.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanContractRepositoryTest {

    @Autowired
    private LoanContractRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LoanContractRepository();
    }

    @Test
    void save() {
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

        repository.save(contractRequest);

        assertTrue(repository.existsById(contractRequest.customerId()));
    }

    @Test
    void replace() {
        var updatedContract = new LoanContractRequest(
                "1-123-12345G",
                50000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        repository.replace(updatedContract);

        assertTrue(repository.existsById(updatedContract.customerId()));
    }

    @Test
    void existsById() {
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
        repository.save(contractRequest);

        assertTrue(repository.existsById(contractRequest.customerId()));

        var nonExistentContractRequest = new LoanContractRequest(
                "2-123-12345G",
                60000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.PENDING),
                        new Approver(Managers.P998ABC, ApprovalStatus.PENDING)
                ),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.now(),
                "Not approved yet"
        );

        assertFalse(repository.existsById(nonExistentContractRequest.customerId()));
    }

    @Test
    void deleteById() {
        var contractRequest = new LoanContractRequest(
                "6-123-12345G",
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
        repository.save(contractRequest);

        assertTrue(repository.existsById(contractRequest.customerId()));

        repository.deleteById(contractRequest.customerId());

        assertFalse(repository.existsById(contractRequest.customerId()));
    }

    @Test
    void statusContractPending() {
        var contractRequest1 = new LoanContractRequest(
                "6-123-12345G",
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

        var contractRequest2 = new LoanContractRequest(
                "2-456-67890H",
                75000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"        );

        repository.save(contractRequest1);
        repository.save(contractRequest2);

        assertTrue(repository.statusContractPending(contractRequest1));
        assertFalse(repository.statusContractPending(contractRequest2));
    }

    @Test
    void findSentContracts() {
        var contractRequest1 = new LoanContractRequest(
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

        var contractRequest2 = new LoanContractRequest(
                "2-456-67890H",
                75000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        var contractRequest3 = new LoanContractRequest(
                "3-789-09876J",
                60000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"        );

        repository.save(contractRequest1);
        repository.save(contractRequest2);
        repository.save(contractRequest3);

        var sentContracts = repository.findSentContracts(1);

        assertEquals(2, sentContracts.size());
    }

    @Test
    void findAllContracts() {
        var contractRequest1 = new LoanContractRequest(
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

        var contractRequest2 = new LoanContractRequest(
                "2-456-67890H",
                75000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        var contractRequest3 = new LoanContractRequest(
                "3-789-09876J",
                60000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        repository.save(contractRequest1);
        repository.save(contractRequest2);
        repository.save(contractRequest3);

        var allContracts = repository.findAllContracts();

        assertEquals(3, allContracts.size());
    }

    @Test
    void calculateSumOfLoanAmounts() {
        var contractRequest1 = new LoanContractRequest(
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

        var contractRequest2 = new LoanContractRequest(
                "2-456-67890H",
                75000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        var contractRequest3 = new LoanContractRequest(
                "3-789-09876J",
                60000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        repository.save(contractRequest1);
        repository.save(contractRequest2);
        repository.save(contractRequest3);

        double expectedSum = 50000.0 + 75000.0 + 60000.0;
        double actualSum = repository.calculateSumOfLoanAmounts();

        assertEquals(expectedSum, actualSum);
    }

    @Test
    void calculateAverageLoanAmount() {
        var contractRequest1 = new LoanContractRequest(
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

        var contractRequest2 = new LoanContractRequest(
                "2-456-67890H",
                75000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        var contractRequest3 = new LoanContractRequest(
                "3-789-09876J",
                60000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        repository.save(contractRequest1);
        repository.save(contractRequest2);
        repository.save(contractRequest3);

        var expectedAverage = 61666.67;
        var actualAverage = repository.calculateAverageLoanAmount();

        assertEquals(expectedAverage, actualAverage);
    }

    @Test
    void findMaxLoanAmount() {
        var contractRequest1 = new LoanContractRequest(
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

        var contractRequest2 = new LoanContractRequest(
                "2-456-67890H",
                75000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        var contractRequest3 = new LoanContractRequest(
                "3-789-09876J",
                60000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        repository.save(contractRequest1);
        repository.save(contractRequest2);
        repository.save(contractRequest3);

        double expectedMaxLoanAmount = 75000.0;
        double actualMaxLoanAmount = repository.findMaxLoanAmount();

        assertEquals(expectedMaxLoanAmount, actualMaxLoanAmount);
    }

    @Test
    void findMinLoanAmount() {
        var contractRequest1 = new LoanContractRequest(
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

        var contractRequest2 = new LoanContractRequest(
                "2-456-67890H",
                75000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        var contractRequest3 = new LoanContractRequest(
                "3-789-09876J",
                60000.0,
                List.of(
                        new Approver(Managers.P998XYZ, ApprovalStatus.APPROVED),
                        new Approver(Managers.P998ABC, ApprovalStatus.APPROVED)
                ),
                LoanType.GENERAL,
                ContractStatus.SENT,
                LocalDateTime.now(),
                "Approved date"
        );

        repository.save(contractRequest1);
        repository.save(contractRequest2);
        repository.save(contractRequest3);

        double expectedMinLoanAmount = 50000.0;
        double actualMinLoanAmount = repository.findMinLoanAmount();

        assertEquals(expectedMinLoanAmount, actualMinLoanAmount);
    }
}