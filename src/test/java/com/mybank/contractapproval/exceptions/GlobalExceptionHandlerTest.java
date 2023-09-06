package com.mybank.contractapproval.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleCustomException() {
        CustomException customException = new CustomException("Custom exception message");
        ResponseEntity<CustomErrorResponse> responseEntity = globalExceptionHandler.handleCustomException(customException);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Custom exception message", responseEntity.getBody().getMessage());
    }

    @Test
    void handleNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("Not found exception message");
        ResponseEntity<CustomErrorResponse> responseEntity = globalExceptionHandler.handleNotFoundException(notFoundException);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Not found exception message", responseEntity.getBody().getMessage());
    }

    @Test
    void handleConflictException() {
        ConflictException conflictException = new ConflictException("Conflict exception message");
        ResponseEntity<CustomErrorResponse> responseEntity = globalExceptionHandler.handleConflictException(conflictException);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals("Conflict exception message", responseEntity.getBody().getMessage());
    }

    @Test
    void handleInvalidDataException() {
        InvalidDataException invalidDataException = new InvalidDataException("Invalid data exception message");
        ResponseEntity<CustomErrorResponse> responseEntity = globalExceptionHandler.handleInvalidDataException(invalidDataException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid data exception message", responseEntity.getBody().getMessage());
    }
}