package com.codillas.loyaltycard.service.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

  private UUID id;
  private String name;
  private String phoneNumber;
  private String email;
  private String password;
  private Status status;
  private Type type;
  private Instant createdAt;
  private Instant updatedAt;
}
