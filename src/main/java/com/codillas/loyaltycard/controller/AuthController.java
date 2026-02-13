package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.SignUpRequestDto;
import com.codillas.loyaltycard.controller.dto.TokenResponseDto;
import com.codillas.loyaltycard.service.CustomerService;
import com.codillas.loyaltycard.service.TokenService;
import com.codillas.loyaltycard.service.model.Customer;
import com.codillas.loyaltycard.service.model.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        Customer customer = customerService.createCustomer(
                signUpRequestDto.getName(),
                signUpRequestDto.getPhoneNumber(),
                signUpRequestDto.getEmail(),
                bCryptPasswordEncoder.encode(signUpRequestDto.getPassword()));

        String token = tokenService.createToken(customer.getId().toString(), Type.CUSTOMER);

        return ResponseEntity.ok(new TokenResponseDto(token));
    }
}
