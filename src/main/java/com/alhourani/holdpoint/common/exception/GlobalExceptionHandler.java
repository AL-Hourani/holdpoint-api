package com.alhourani.holdpoint.common.exception;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
        ResourceNotFoundException exception,
        HttpServletRequest request
    ){
        return buildResponse(
            HttpStatus.NOT_FOUND, 
            exception.getMessage(), 
            request.getRequestURI()
        );
    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public  ResponseEntity<ApiErrorResponse> handleEmailAlreadyExist(
        EmailAlreadyExistsException exception,
          HttpServletRequest request
    ){
        return buildResponse(
            HttpStatus.CONFLICT, 
            exception.getMessage(), 
            request.getRequestURI()  
        );
    }



    // RESOURCE CONFLICT
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyExists(
            ResourceAlreadyExistsException exception,
            HttpServletRequest request) {

        return buildResponse(
            HttpStatus.CONFLICT,
            exception.getMessage(),
            request.getRequestURI()
        );
    }



    @ExceptionHandler(InvalidCapacityException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCapacity(
            InvalidCapacityException exception,
            HttpServletRequest request) {

        return buildResponse(
            HttpStatus.BAD_REQUEST,
            exception.getMessage(),
            request.getRequestURI()
        );
    }








     @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");

        return buildResponse(
            HttpStatus.BAD_REQUEST,
            message,
            request.getRequestURI()
        );
    }



    

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        return buildResponse(
            HttpStatus.CONFLICT,
            "The request violates a data integrity constraint",
            request.getRequestURI()
        );
    }





    // EXCEPTION UTILE
     private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String path) {

        ApiErrorResponse response = new ApiErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path
        );

        return ResponseEntity.status(status).body(response);
    }// end build response
}
