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
public class CustomerDto {

    private UUID id;
    private String name;
    private String phoneNumber;
    private String email;
    private Status status;
    private Integer balance;
    private UUID cardId;
    private Instant createdAt;
    private Instant updatedAt;
}
