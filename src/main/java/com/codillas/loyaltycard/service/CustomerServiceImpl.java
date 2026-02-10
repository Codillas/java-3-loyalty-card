package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.exception.CustomerAlreadyExistsException;
import com.codillas.loyaltycard.exception.CustomerNotFoundException;
import com.codillas.loyaltycard.service.model.Customer;
import com.codillas.loyaltycard.service.model.Status;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private HashMap<UUID, Customer> customerHashMap = new HashMap<>();

    @Override
    public Customer createCustomer(String name, String phoneNumber, String email, String password) {

        log.info("Attempting to create a customer with email {}", email);

        Optional<Customer> optionalCustomer = customerHashMap.values().stream()
                .filter(customer -> customer.getEmail().equals(email))
                .findFirst();

        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException(email);
        }

        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setPassword(password);

        Instant now = Instant.now();

        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);
        customer.setStatus(Status.ACTIVE);
        customer.setId(UUID.randomUUID());

        customerHashMap.put(customer.getId(), customer);

        log.info("Successfully created a customer with email {}", email);

        return customer;
    }

    @Override
    public List<Customer> getCustomers() {
        return customerHashMap.values().stream().toList();
    }

    @Override
    public Customer getCustomer(UUID customerId) {
        Customer customer = customerHashMap.get(customerId);

        if (customer != null) {
            return customer;
        } else {
            throw new CustomerNotFoundException(customerId);
        }
    }

    @Override
    public Customer updateCustomer(UUID customerId, String name, String phoneNumber, String email) {
        Customer customer = customerHashMap.get(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }

        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setUpdatedAt(Instant.now());

        customerHashMap.put(customer.getId(), customer);

        return customer;
    }

    @Override
    public Customer activateCustomer(UUID customerId) {
        Customer customer = customerHashMap.get(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }

        customer.setStatus(Status.ACTIVE);
        customer.setUpdatedAt(Instant.now());
        customerHashMap.put(customer.getId(), customer);

        return customer;
    }

    @Override
    public Customer blockCustomer(UUID customerId) {
        Customer customer = customerHashMap.get(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }

        customer.setStatus(Status.BLOCKED);
        customer.setUpdatedAt(Instant.now());
        customerHashMap.put(customer.getId(), customer);

        return customer;
    }
}
