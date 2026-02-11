package com.example.spvms.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void handleValidationExceptions_ReturnsValidationErrors() {
        FieldError fieldError = new FieldError("user", "email", "Invalid email");

        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError));

        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleValidationExceptions(validationException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validation Failed", response.getBody().get("error"));
        assertTrue(response.getBody().containsKey("errors"));
    }

    @Test
    void handleBadRequest_ReturnsCorrectResponse() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid input");

        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleBadRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Invalid input", response.getBody().get("message"));
    }

    @Test
    void handleRuntime_ReturnsInternalServerError() {
        RuntimeException exception = new RuntimeException("Something went wrong");

        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleRuntime(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Something went wrong", response.getBody().get("message"));
    }

    @Test
    void handleAccessDenied_ReturnsForbidden() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleAccessDenied(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().get("status"));
        assertEquals("Access Denied", response.getBody().get("error"));
    }

    @Test
    void handleBadCredentials_ReturnsUnauthorized() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleBadCredentials(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }

    @Test
    void handleDbIntegrity_DuplicateEntry_ReturnsConflict() {
        org.springframework.dao.DataIntegrityViolationException exception = 
            new org.springframework.dao.DataIntegrityViolationException(
                "Duplicate entry", 
                new Exception("Duplicate entry 'test' for key 'email'")
            );

        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleDbIntegrity(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
        assertTrue(response.getBody().get("message").toString().contains("already exists"));
    }

    @Test
    void handleDbIntegrity_ForeignKey_ReturnsConflict() {
        org.springframework.dao.DataIntegrityViolationException exception = 
            new org.springframework.dao.DataIntegrityViolationException(
                "Foreign key constraint", 
                new Exception("foreign key constraint fails")
            );

        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleDbIntegrity(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").toString().contains("existing references"));
    }

    @Test
    void handleGeneral_ReturnsInternalServerError() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<Map<String, Object>> response = 
            exceptionHandler.handleGeneral(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("An unexpected error occurred", response.getBody().get("message"));
    }
}
