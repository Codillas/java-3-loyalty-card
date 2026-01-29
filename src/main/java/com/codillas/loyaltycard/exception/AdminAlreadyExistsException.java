package com.codillas.loyaltycard.exception;


public class AdminAlreadyExistsException extends RuntimeException {

  public AdminAlreadyExistsException(String email) {
    super("Admin with email " + email + " already exists.");
  }
}
