package com.codillas.loyaltycard.exception;


public class AdminIsBlockedException extends RuntimeException {

  public AdminIsBlockedException() {
    super("Admin is blocked");
  }
}
