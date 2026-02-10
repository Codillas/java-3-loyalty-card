package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.SignUpRequestDto;
import com.codillas.loyaltycard.controller.dto.TokenResponseDto;
import com.codillas.loyaltycard.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final CustomerService customerService;

    @Autowired
    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        customerService.createCustomer(
                signUpRequestDto.getName(),
                signUpRequestDto.getPhoneNumber(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword());

        // Placeholder token generation as no auth provider is integrated yet
        String token = "placeholder-jwt-token";

        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
