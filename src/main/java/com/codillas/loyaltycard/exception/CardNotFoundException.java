package com.codillas.loyaltycard.exception;

import java.util.UUID;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(UUID cardId) {
        super("Card with id " + cardId.toString() + " was not found.");
    }
}
