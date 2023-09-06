package com.mybank.contractapproval.statistics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApproverStatisticsTest {

    @Test
    void getUsername() {
        String username = "testUsername";
        ApproverStatistics statistics = new ApproverStatistics(username, 10);

        String result = statistics.getUsername();

        assertEquals(username, result);
    }

    @Test
    void getNumberOfApprovals() {
        long numberOfApprovals = 5;
        ApproverStatistics statistics = new ApproverStatistics("testUsername", numberOfApprovals);

        long result = statistics.getNumberOfApprovals();

        assertEquals(numberOfApprovals, result);
    }

    @Test
    void setUsername() {
        ApproverStatistics statistics = new ApproverStatistics("oldUsername", 5);
        String newUsername = "newUsername";

        statistics.setUsername(newUsername);

        assertEquals(newUsername, statistics.getUsername());
    }

    @Test
    void setNumberOfApprovals() {
        ApproverStatistics statistics = new ApproverStatistics("testUsername", 5);
        long newNumberOfApprovals = 10;

        statistics.setNumberOfApprovals(newNumberOfApprovals);

        assertEquals(newNumberOfApprovals, statistics.getNumberOfApprovals());
    }
}