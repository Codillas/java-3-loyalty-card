package com.codillas.loyaltycard.service.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    private UUID id;
    private UUID customerId;
    private int balance;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
}
