package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.CustomerDto;
import com.codillas.loyaltycard.mapper.CustomerMapper;
import com.codillas.loyaltycard.service.CustomerService;
import com.codillas.loyaltycard.service.model.Customer;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @Secured("ADMIN")
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDto>> getCustomers() {
        List<Customer> customerList = customerService.getCustomers();
        List<CustomerDto> customerDtoList = customerList.stream().map(customerMapper::toDto).toList();
        return ResponseEntity.ok().body(customerDtoList);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN') or #customerId.toString() == authentication.name")
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable UUID customerId) {
        Customer customer = customerService.getCustomer(customerId);
        CustomerDto customerDto = customerMapper.toDto(customer);
        return ResponseEntity.ok().body(customerDto);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN') or #customerId.toString() == authentication.name")
    @PutMapping("/customers/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable UUID customerId, @RequestBody CustomerDto customerDto) {
        Customer customer = customerService.updateCustomer(
                customerId,
                customerDto.getName(),
                customerDto.getPhoneNumber(),
                customerDto.getEmail());
        return ResponseEntity.ok().body(customerMapper.toDto(customer));
    }

    @Secured("ADMIN")
    @PutMapping("/customers/{customerId}/activate")
    public ResponseEntity<CustomerDto> activateCustomer(@PathVariable UUID customerId) {
        Customer customer = customerService.activateCustomer(customerId);
        return ResponseEntity.ok().body(customerMapper.toDto(customer));
    }

    @Secured("ADMIN")
    @PutMapping("/customers/{customerId}/block")
    public ResponseEntity<CustomerDto> blockCustomer(@PathVariable UUID customerId) {
        Customer customer = customerService.blockCustomer(customerId);
        return ResponseEntity.ok().body(customerMapper.toDto(customer));
    }
}
