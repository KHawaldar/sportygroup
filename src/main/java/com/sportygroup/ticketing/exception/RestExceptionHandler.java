package com.sportygroup.ticketing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>>  handleValidationError(MethodArgumentNotValidException ex){

        var errorMap = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach(error->
                errorMap.put(error.getField(), error.getDefaultMessage())
                );
       return ResponseEntity.badRequest().body(errorMap);

    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTicketNotFoundException(TicketNotFoundException ex){
        var errorMap = new HashMap<String, String>();
        errorMap.put("statuscode", "404");
        errorMap.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }
    @ExceptionHandler(LockAcquisitionException.class)
    public ResponseEntity<Map<String, String>>  lockAcquisitionException(LockAcquisitionException ex){
        var errorMap = new HashMap<String, String>();
        errorMap.put("statuscode", HttpStatus.LOCKED.toString());
        errorMap.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.LOCKED).body(errorMap);
    }
    @ExceptionHandler(TicketAlreadyAssignedException.class)
    public ResponseEntity<Map<String, String>> ticketAlreadyAssignedException(TicketAlreadyAssignedException ex){
        var errorMap = new HashMap<String, String>();
        errorMap.put("statuscode", HttpStatus.CONFLICT.toString());
        errorMap.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
    }

}
