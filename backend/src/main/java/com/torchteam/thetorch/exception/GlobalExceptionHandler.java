package com.torchteam.thetorch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation failures and business rule violations (e.g., "Only admin can delete")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Handles invalid state transitions (e.g., "Cannot pin to ungrown tree", "Not connected to Strava")
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request); // 409 Conflict is perfect for state issues
    }

    // Handles database lookups that fail (e.g., repository.findById().orElseThrow())
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        // We overwrite the default Java message to be more API-friendly
        return buildResponse(HttpStatus.NOT_FOUND, "The requested resource could not be found.", request);
    }

    @ExceptionHandler(IncompleteAccountException.class)
    public ResponseEntity<ApiErrorResponse> handleIncompleteAccountException(
            IncompleteAccountException ex,
            WebRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Fallback for any other unexpected crashes (NullPointers, Database disconnects, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        // Log the actual exception here in a real production app!
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal server error occurred.", request);
    }

    // Helper method to format the response
    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false).replace("uri=", "") // Cleans up the path string
        );
        return new ResponseEntity<>(response, status);
    }
}