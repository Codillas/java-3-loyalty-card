package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.LoginRequestDto;
import com.codillas.loyaltycard.controller.dto.SignUpRequestDto;
import com.codillas.loyaltycard.controller.dto.TokenResponseDto;
import com.codillas.loyaltycard.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {

        String token = authService.signUp(
                signUpRequestDto.getName(),
                signUpRequestDto.getPhoneNumber(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword());

        return ResponseEntity.ok(new TokenResponseDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        String token = authService.loginCustomer(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        return ResponseEntity.ok(new TokenResponseDto(token));
    }


}
