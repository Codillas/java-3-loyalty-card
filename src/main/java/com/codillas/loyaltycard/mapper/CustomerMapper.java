package com.codillas.loyaltycard.mapper;

import com.codillas.loyaltycard.controller.dto.CustomerDto;
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
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }
}
