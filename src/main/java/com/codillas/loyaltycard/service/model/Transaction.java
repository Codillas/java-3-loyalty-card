package com.codillas.loyaltycard.service.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private UUID id;
    private UUID cardId;
    private UUID adminId;
    private Direction direction;
    private int amount;
    private TransactionStatus status;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;
}
