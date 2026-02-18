package com.codillas.loyaltycard.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(UUID transactionId) {
        super("Transaction with id " + transactionId.toString() + " was not found.");
    }
}
