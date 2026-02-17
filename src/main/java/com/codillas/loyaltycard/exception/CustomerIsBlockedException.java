package com.codillas.loyaltycard.exception;


public class CustomerIsBlockedException extends RuntimeException {

  public CustomerIsBlockedException() {
    super("Customer is blocked");
  }
}
