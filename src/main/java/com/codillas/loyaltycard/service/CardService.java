package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.service.model.Card;

import java.util.UUID;

public interface CardService {

    Card createCard(UUID customerId);

    Card getCard(UUID cardId);

    Card activateCard(UUID cardId);

    Card blockCard(UUID cardId);

}
