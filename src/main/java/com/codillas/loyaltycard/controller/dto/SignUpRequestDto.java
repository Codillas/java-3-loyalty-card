package com.codillas.loyaltycard.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

  private String name;
  private String phoneNumber;
  private String email;
  private String password;
}
