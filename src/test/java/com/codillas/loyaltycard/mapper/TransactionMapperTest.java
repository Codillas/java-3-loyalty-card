package com.codillas.loyaltycard.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.codillas.loyaltycard.controller.dto.DirectionDto;
import com.codillas.loyaltycard.controller.dto.TransactionDto;
import com.codillas.loyaltycard.controller.dto.TransactionStatusDto;
import com.codillas.loyaltycard.repository.entity.Direction;
import com.codillas.loyaltycard.repository.entity.TransactionEntity;
import com.codillas.loyaltycard.repository.entity.TransactionStatus;
import com.codillas.loyaltycard.service.model.Transaction;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransactionMapperTest {

    private final TransactionMapper transactionMapper = new TransactionMapper();

    @Test
    void toDto_shouldMapAllFields() {
        // given
        UUID id = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Instant now = Instant.now();

        Transaction transaction = new Transaction(id, cardId, adminId,
                com.codillas.loyaltycard.service.model.Direction.IN, 100,
                com.codillas.loyaltycard.service.model.TransactionStatus.SUCCESS,
                "Test note", now, now);

        // when
        TransactionDto dto = transactionMapper.toDto(transaction);

        // then
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getCardId()).isEqualTo(cardId);
        assertThat(dto.getAdminId()).isEqualTo(adminId);
        assertThat(dto.getDirection()).isEqualTo(DirectionDto.IN);
        assertThat(dto.getAmount()).isEqualTo(100);
        assertThat(dto.getStatus()).isEqualTo(TransactionStatusDto.SUCCESS);
        assertThat(dto.getNote()).isEqualTo("Test note");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDto_shouldMapOutDirectionAndCancelledStatus() {
        // given
        Transaction transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), com.codillas.loyaltycard.service.model.Direction.OUT, 50,
                com.codillas.loyaltycard.service.model.TransactionStatus.CANCELLED,
                null, Instant.now(), Instant.now());

        // when
        TransactionDto dto = transactionMapper.toDto(transaction);

        // then
        assertThat(dto.getDirection()).isEqualTo(DirectionDto.OUT);
        assertThat(dto.getStatus()).isEqualTo(TransactionStatusDto.CANCELLED);
    }

    @Test
    void toDomain_shouldMapAllFields() {
        // given
        UUID id = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Instant now = Instant.now();

        TransactionEntity entity = new TransactionEntity();
        entity.setId(id);
        entity.setCardId(cardId);
        entity.setAdminId(adminId);
        entity.setDirection(Direction.IN);
        entity.setAmount(200);
        entity.setStatus(TransactionStatus.SUCCESS);
        entity.setNote("Entity note");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // when
        Transaction transaction = transactionMapper.toDomain(entity);

        // then
        assertThat(transaction.getId()).isEqualTo(id);
        assertThat(transaction.getCardId()).isEqualTo(cardId);
        assertThat(transaction.getAdminId()).isEqualTo(adminId);
        assertThat(transaction.getDirection()).isEqualTo(com.codillas.loyaltycard.service.model.Direction.IN);
        assertThat(transaction.getAmount()).isEqualTo(200);
        assertThat(transaction.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.TransactionStatus.SUCCESS);
        assertThat(transaction.getNote()).isEqualTo("Entity note");
        assertThat(transaction.getCreatedAt()).isEqualTo(now);
        assertThat(transaction.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDomain_shouldMapCancelledStatusAndOutDirection() {
        // given
        TransactionEntity entity = new TransactionEntity();
        entity.setId(UUID.randomUUID());
        entity.setCardId(UUID.randomUUID());
        entity.setAdminId(UUID.randomUUID());
        entity.setDirection(Direction.OUT);
        entity.setAmount(75);
        entity.setStatus(TransactionStatus.CANCELLED);
        entity.setNote(null);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        // when
        Transaction transaction = transactionMapper.toDomain(entity);

        // then
        assertThat(transaction.getDirection()).isEqualTo(com.codillas.loyaltycard.service.model.Direction.OUT);
        assertThat(transaction.getStatus())
                .isEqualTo(com.codillas.loyaltycard.service.model.TransactionStatus.CANCELLED);
        assertThat(transaction.getNote()).isNull();
    }
}
