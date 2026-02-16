package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.service.model.Customer;
import java.util.List;
import java.util.UUID;

public interface CustomerService {

    Customer createCustomer(String name, String phoneNumber, String email, String password);

    List<Customer> getCustomers();

    Customer getCustomer(UUID customerId);

    Customer getCustomer(String email);

    Customer updateCustomer(UUID customerId, String name, String phoneNumber, String email);

    Customer activateCustomer(UUID customerId);

    Customer blockCustomer(UUID customerId);
}
