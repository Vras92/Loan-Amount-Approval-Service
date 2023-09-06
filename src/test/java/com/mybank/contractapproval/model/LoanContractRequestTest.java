package com.mybank.contractapproval.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybank.contractapproval.model.enums.ApprovalStatus;
import com.mybank.contractapproval.model.enums.ContractStatus;
import com.mybank.contractapproval.model.enums.LoanType;
import com.mybank.contractapproval.model.enums.Managers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class LoanContractRequestTest {

    @Autowired
    JacksonTester<LoanContractRequest> tester;
    @Autowired
    JacksonTester<List<LoanContractRequest>> testerList;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializationJsonFile() throws IOException {
        var record = new LoanContractRequest(
                "1-123-12345G",
                60000.0,
                List.of(new Approver(Managers.P998XYZ, ApprovalStatus.PENDING)),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.parse("2023-09-05T18:21:24.0450078"),
                "not approved yet"
        );

        JsonContent<LoanContractRequest> json = tester.write(record);
        JsonNode expectedJson = objectMapper.readTree(new ClassPathResource("contract.json").getInputStream());
        JsonNode actualJson = objectMapper.readTree(json.getJson());

        assertEquals(expectedJson.size(), actualJson.size());

        for (int i = 0; i < expectedJson.size(); i++) {
            JsonNode expected = expectedJson.get(i);
            JsonNode actual = actualJson.get(i);

            assertEquals(expected, actual);
        }
    }

    @Test
    void testListLoanContractRequestSerialization() throws IOException {
        List<LoanContractRequest> records =
                getLoanContractRequests();
        JsonContent<List<LoanContractRequest>> json = testerList.write(records);

        JsonNode expectedJsonArray =
                objectMapper.readTree(new ClassPathResource("contracts.json").getInputStream());
        JsonNode actualJsonArray = objectMapper.readTree(json.getJson());

        assertEquals(expectedJsonArray.size(), actualJsonArray.size());

        for (int i = 0; i < expectedJsonArray.size(); i++) {
            JsonNode expectedJson = expectedJsonArray.get(i);
            JsonNode actualJson = actualJsonArray.get(i);

            assertEquals(expectedJson, actualJson);
        }
    }

    @Test
    void testDeserialization() throws IOException {
        var record = tester.read(new ClassPathResource(
                "contract.json")).getObject();
        assertEquals("1-123-12345G", record.customerId());
        assertEquals(60000.0, record.loanAmount());
        assertEquals(Managers.P998XYZ, record.approvers().get(0).username());
        assertEquals(ApprovalStatus.PENDING, record.approvers().get(0).status());
        assertEquals(LoanType.GENERAL, record.loanType());
        assertEquals(ContractStatus.PENDING, record.loanContractStatus());
        assertEquals(LocalDateTime.parse("2023-09-05T18:21:24.0450078"), record.createdDate());
        assertEquals("Not approved yet", record.sentToCustomerDate());
    }

    private static List<LoanContractRequest> getLoanContractRequests() {
        var record = new LoanContractRequest(
                "1-123-12345G",
                60000.0,
                List.of(new Approver(Managers.P998XYZ, ApprovalStatus.PENDING)),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.parse("2023-09-05T18:21:24.0450078"),
                "Not approved yet"
        );
        var record2 = new LoanContractRequest(
                "2-123-12345G",
                4050.9,
                List.of(new Approver(Managers.P998ABC, ApprovalStatus.PENDING)),
                LoanType.GENERAL,
                ContractStatus.PENDING,
                LocalDateTime.parse("2023-09-05T19:17:06.8549747"),
                "Not approved yet"
        );

        return List.of(record, record2);
    }
}