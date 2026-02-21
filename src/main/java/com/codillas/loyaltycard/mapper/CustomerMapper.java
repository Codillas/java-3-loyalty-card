package com.codillas.loyaltycard.mapper;

import com.codillas.loyaltycard.controller.dto.CustomerDto;
import com.codillas.loyaltycard.repository.entity.CustomerEntity;
import com.codillas.loyaltycard.service.model.Customer;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                customer.getCardId(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }

    public Customer toDomain(CustomerEntity entity) {
        Integer balance = entity.getCard() != null ? entity.getCard().getBalance() : null;
        UUID cardId = entity.getCard() != null ? entity.getCard().getId() : null;
        return new Customer(
                entity.getId(),
                entity.getName(),
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getPassword(),
                com.codillas.loyaltycard.service.model.Status.valueOf(entity.getStatus().name()),
                balance,
                cardId,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
