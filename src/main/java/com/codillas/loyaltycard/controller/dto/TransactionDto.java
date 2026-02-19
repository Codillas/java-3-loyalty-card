package com.codillas.loyaltycard.controller.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private UUID id;
    private UUID cardId;
    private UUID adminId;
    private DirectionDto direction;
    private int amount;
    private TransactionStatusDto status;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;
}
