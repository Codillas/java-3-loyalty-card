package com.codillas.loyaltycard.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Customer with id " + customerId + " not found");
    }

    public CustomerNotFoundException(String email) {
        super("Customer with email " + email + " not found");
    }
}
