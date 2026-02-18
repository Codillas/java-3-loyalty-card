package com.codillas.loyaltycard.mapper;

import com.codillas.loyaltycard.controller.dto.CustomerDto;
import com.codillas.loyaltycard.repository.entity.CustomerEntity;
import com.codillas.loyaltycard.service.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDto toDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getStatus(),
                customer.getBalance(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }

    public Customer toDomain(CustomerEntity entity) {
        Integer balance = entity.getCard() != null ? entity.getCard().getBalance() : null;
        return new Customer(
                entity.getId(),
                entity.getName(),
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getPassword(),
                com.codillas.loyaltycard.service.model.Status.valueOf(entity.getStatus().name()),
                balance,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.getId());
        entity.setName(customer.getName());
        entity.setPhoneNumber(customer.getPhoneNumber());
        entity.setEmail(customer.getEmail());
        entity.setPassword(customer.getPassword());
        entity.setStatus(com.codillas.loyaltycard.repository.entity.Status.valueOf(customer.getStatus().name()));
        entity.setCreatedAt(customer.getCreatedAt());
        entity.setUpdatedAt(customer.getUpdatedAt());
        return entity;
    }
}
