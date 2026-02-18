package com.codillas.loyaltycard.mapper;

import com.codillas.loyaltycard.controller.dto.DirectionDto;
import com.codillas.loyaltycard.controller.dto.TransactionDto;
import com.codillas.loyaltycard.controller.dto.TransactionStatusDto;
import com.codillas.loyaltycard.repository.entity.TransactionEntity;
import com.codillas.loyaltycard.service.model.Direction;
import com.codillas.loyaltycard.service.model.Transaction;
import com.codillas.loyaltycard.service.model.TransactionStatus;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getCardId(),
                transaction.getAdminId(),
                DirectionDto.valueOf(transaction.getDirection().name()),
                transaction.getAmount(),
                TransactionStatusDto.valueOf(transaction.getStatus().name()),
                transaction.getNote(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt());
    }

    public Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getCardId(),
                entity.getAdminId(),
                Direction.valueOf(entity.getDirection().name()),
                entity.getAmount(),
                TransactionStatus.valueOf(entity.getStatus().name()),
                entity.getNote(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
