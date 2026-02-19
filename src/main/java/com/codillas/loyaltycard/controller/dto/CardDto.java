package com.codillas.loyaltycard.controller.dto;

import com.codillas.loyaltycard.service.model.Status;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

    private UUID id;
    private UUID customerId;
    private int balance;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
}
