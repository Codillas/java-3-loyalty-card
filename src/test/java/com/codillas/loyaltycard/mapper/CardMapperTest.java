package com.codillas.loyaltycard.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.codillas.loyaltycard.controller.dto.CardDto;
import com.codillas.loyaltycard.repository.entity.CardEntity;
import com.codillas.loyaltycard.repository.entity.CustomerEntity;
import com.codillas.loyaltycard.repository.entity.Status;
import com.codillas.loyaltycard.service.model.Card;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CardMapperTest {

    private final CardMapper cardMapper = new CardMapper();

    @Test
    void toDto_shouldMapAllFields() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant now = Instant.now();

        Card card = new Card(cardId, customerId, 100,
                com.codillas.loyaltycard.service.model.Status.ACTIVE, now, now);

        // when
        CardDto dto = cardMapper.toDto(card);

        // then
        assertThat(dto.getId()).isEqualTo(cardId);
        assertThat(dto.getCustomerId()).isEqualTo(customerId);
        assertThat(dto.getBalance()).isEqualTo(100);
        assertThat(dto.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.Status.ACTIVE);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDomain_shouldMapAllFields() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant now = Instant.now();

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        CardEntity entity = new CardEntity();
        entity.setId(cardId);
        entity.setCustomer(customerEntity);
        entity.setBalance(250);
        entity.setStatus(Status.ACTIVE);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // when
        Card card = cardMapper.toDomain(entity);

        // then
        assertThat(card.getId()).isEqualTo(cardId);
        assertThat(card.getCustomerId()).isEqualTo(customerId);
        assertThat(card.getBalance()).isEqualTo(250);
        assertThat(card.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.Status.ACTIVE);
        assertThat(card.getCreatedAt()).isEqualTo(now);
        assertThat(card.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDomain_shouldMapBlockedStatus() {
        // given
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(UUID.randomUUID());

        CardEntity entity = new CardEntity();
        entity.setId(UUID.randomUUID());
        entity.setCustomer(customerEntity);
        entity.setBalance(0);
        entity.setStatus(Status.BLOCKED);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        // when
        Card card = cardMapper.toDomain(entity);

        // then
        assertThat(card.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.Status.BLOCKED);
    }
}
