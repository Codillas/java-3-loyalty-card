package com.codillas.loyaltycard.controller.exceptionhandler;

import com.codillas.loyaltycard.controller.dto.ErrorDto;
import com.codillas.loyaltycard.exception.AdminAlreadyExistsException;
import com.codillas.loyaltycard.exception.AdminNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<ErrorDto> handleAdminNotFoundException(AdminNotFoundException e) {

        ErrorDto errorDto = new ErrorDto(e.getMessage());

        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @ExceptionHandler(AdminAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleAdminAlreadyExistsException(AdminAlreadyExistsException e) {

        ErrorDto errorDto = new ErrorDto(e.getMessage());

        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }
}
