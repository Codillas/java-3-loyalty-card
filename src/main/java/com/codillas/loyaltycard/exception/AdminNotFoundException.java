package com.codillas.loyaltycard.exception;

import java.util.UUID;

public class AdminNotFoundException extends RuntimeException {

  public AdminNotFoundException(UUID adminId) {
    super("Admin with id " + adminId.toString() + " was not found.");
  }

  public AdminNotFoundException(String email) {
    super("Admin with email " + email + " was not found.");
  }
}
