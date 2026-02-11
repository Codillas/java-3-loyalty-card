package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.exception.CustomerAlreadyExistsException;
import com.codillas.loyaltycard.exception.CustomerNotFoundException;
import com.codillas.loyaltycard.mapper.CustomerMapper;
import com.codillas.loyaltycard.repository.CustomerRepository;
import com.codillas.loyaltycard.repository.entity.CustomerEntity;
import com.codillas.loyaltycard.service.model.Customer;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public Customer createCustomer(String name, String phoneNumber, String email, String password) {

        log.info("Attempting to create a customer with email {}", email);

        Optional<CustomerEntity> optionalCustomer = customerRepository.findByEmail(email);

        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException(email);
        }

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName(name);
        customerEntity.setPhoneNumber(phoneNumber);
        customerEntity.setEmail(email);
        customerEntity.setPassword(password);

        Instant now = Instant.now();

        customerEntity.setCreatedAt(now);
        customerEntity.setUpdatedAt(now);
        customerEntity.setStatus(com.codillas.loyaltycard.repository.entity.Status.ACTIVE);

        CustomerEntity savedCustomerEntity = customerRepository.save(customerEntity);

        log.info("Successfully created a customer with email {}", email);

        return customerMapper.toDomain(savedCustomerEntity);
    }

    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDomain)
                .toList();
    }

    @Override
    public Customer getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toDomain)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    @Override
    public Customer updateCustomer(UUID customerId, String name, String phoneNumber, String email) {
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customerEntity.setName(name);
        customerEntity.setPhoneNumber(phoneNumber);
        customerEntity.setEmail(email);
        customerEntity.setUpdatedAt(Instant.now());

        CustomerEntity savedEntity = customerRepository.save(customerEntity);

        return customerMapper.toDomain(savedEntity);
    }

    @Override
    public Customer activateCustomer(UUID customerId) {
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customerEntity.setStatus(com.codillas.loyaltycard.repository.entity.Status.ACTIVE);
        customerEntity.setUpdatedAt(Instant.now());

        CustomerEntity savedEntity = customerRepository.save(customerEntity);

        return customerMapper.toDomain(savedEntity);
    }

    @Override
    public Customer blockCustomer(UUID customerId) {
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customerEntity.setStatus(com.codillas.loyaltycard.repository.entity.Status.BLOCKED);
        customerEntity.setUpdatedAt(Instant.now());

        CustomerEntity savedEntity = customerRepository.save(customerEntity);

        return customerMapper.toDomain(savedEntity);
    }
}
