package com.codillas.loyaltycard.controller.exceptionhandler;

import com.codillas.loyaltycard.controller.dto.ErrorDto;
import com.codillas.loyaltycard.exception.*;
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

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCustomerNotFoundException(CustomerNotFoundException e) {

        ErrorDto errorDto = new ErrorDto(e.getMessage());

        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }


    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleCustomerAlreadyExistsException(CustomerAlreadyExistsException e) {

        ErrorDto errorDto = new ErrorDto(e.getMessage());

        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handleInvalidCredentialsException(InvalidCredentialsException e) {

        ErrorDto errorDto = new ErrorDto(e.getMessage());

        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
    }

    @ExceptionHandler(CustomerIsBlockedException.class)
    public ResponseEntity<ErrorDto> handleCustomerIsBlockedException(CustomerIsBlockedException e) {

        ErrorDto errorDto = new ErrorDto(e.getMessage());

        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }

    @ExceptionHandler(AdminIsBlockedException.class)
    public ResponseEntity<ErrorDto> handleAdminIsBlockedException(AdminIsBlockedException e) {

        ErrorDto errorDto = new ErrorDto(e.getMessage());

        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }
}
