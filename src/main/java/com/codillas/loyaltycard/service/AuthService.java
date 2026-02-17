package com.codillas.loyaltycard.service;

public interface AuthService {

    String signUp(String name, String phoneNumber, String email, String password);

    String loginCustomer(String email, String password);

    String loginAdmin(String email, String password);

}
