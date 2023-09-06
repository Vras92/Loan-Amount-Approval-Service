package com.mybank.contractapproval.exceptions;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomErrorResponseTest {

    @Test
    void getTimestamp() {
        LocalDateTime timestamp = LocalDateTime.now();
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setTimestamp(timestamp);
        assertEquals(timestamp, errorResponse.getTimestamp());
    }

    @Test
    void getStatus() {
        int status = 404;
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setStatus(status);
        assertEquals(status, errorResponse.getStatus());
    }

    @Test
    void getError() {
        String error = "Not Found";
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setError(error);
        assertEquals(error, errorResponse.getError());
    }

    @Test
    void getMessage() {
        String message = "Resource not found";
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setMessage(message);
        assertEquals(message, errorResponse.getMessage());
    }

    @Test
    void setTimestamp() {
        LocalDateTime timestamp = LocalDateTime.now();
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setTimestamp(timestamp);
        assertEquals(timestamp, errorResponse.getTimestamp());
    }

    @Test
    void setStatus() {
        int status = 500;
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setStatus(status);
        assertEquals(status, errorResponse.getStatus());
    }

    @Test
    void setError() {
        String error = "Internal Server Error";
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setError(error);
        assertEquals(error, errorResponse.getError());
    }

    @Test
    void setMessage() {
        String message = "An unexpected error occurred";
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setMessage(message);
        assertEquals(message, errorResponse.getMessage());
    }
}