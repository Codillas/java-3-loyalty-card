package com.codillas.loyaltycard.mapper;

import com.codillas.loyaltycard.controller.dto.CardDto;
import com.codillas.loyaltycard.repository.entity.CardEntity;
import com.codillas.loyaltycard.service.model.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardDto toDto(Card card) {
        return new CardDto(
                card.getId(),
                card.getCustomerId(),
                card.getBalance(),
                card.getStatus(),
                card.getCreatedAt(),
                card.getUpdatedAt());
    }

    public Card toDomain(CardEntity entity) {
        return new Card(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getBalance(),
                com.codillas.loyaltycard.service.model.Status.valueOf(entity.getStatus().name()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
