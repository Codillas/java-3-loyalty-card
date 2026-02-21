package com.codillas.loyaltycard.service.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private UUID id;
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private Status status;
    private Integer balance;
    private UUID cardId;
    private Instant createdAt;
    private Instant updatedAt;
}
