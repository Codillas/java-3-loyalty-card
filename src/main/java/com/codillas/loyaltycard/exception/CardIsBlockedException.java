package com.codillas.loyaltycard.exception;

public class CardIsBlockedException extends RuntimeException {

    public CardIsBlockedException() {
        super("Card is blocked.");
    }
}
