package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.*;
import com.codillas.loyaltycard.mapper.AdminMapper;
import com.codillas.loyaltycard.service.AdminService;
import com.codillas.loyaltycard.service.AuthService;
import com.codillas.loyaltycard.service.model.Admin;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;
  private final AdminMapper adminMapper;
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {

    String token =
        authService.loginAdmin(loginRequestDto.getEmail(), loginRequestDto.getPassword());

    return ResponseEntity.ok(new TokenResponseDto(token));
  }

  @PostMapping
  public ResponseEntity<AdminDto> createAdmin(@RequestBody SignUpRequestDto signUpRequestDto) {

    Admin admin =
        adminService.createAdmin(
            signUpRequestDto.getName(),
            signUpRequestDto.getPhoneNumber(),
            signUpRequestDto.getEmail(),
            signUpRequestDto.getPassword());

    AdminDto adminDto = adminMapper.toDto(admin);

    return ResponseEntity.status(HttpStatus.CREATED).body(adminDto);
  }

  @GetMapping
  public ResponseEntity<List<AdminDto>> getAdmins() {

    List<Admin> adminList = adminService.getAdmins();

    List<AdminDto> adminDtoList = adminList.stream().map(adminMapper::toDto).toList();

    return ResponseEntity.ok().body(adminDtoList);
  }

  @GetMapping("/{adminId}")
  public ResponseEntity<AdminDto> getAdmin(@PathVariable UUID adminId) {

    Admin admin = adminService.getAdmin(adminId);

    AdminDto adminDto = adminMapper.toDto(admin);

    return ResponseEntity.ok().body(adminDto);
  }

  @PutMapping("/{adminId}/activate")
  public ResponseEntity<AdminDto> activateAdmin(@PathVariable UUID adminId) {

    Admin admin = adminService.activateAdmin(adminId);

    AdminDto adminDto = adminMapper.toDto(admin);

    return ResponseEntity.ok().body(adminDto);
  }

  @PutMapping("/{adminId}/block")
  public ResponseEntity<AdminDto> blockAdmin(@PathVariable UUID adminId) {

    Admin admin = adminService.blockAdmin(adminId);

    AdminDto adminDto = adminMapper.toDto(admin);

    return ResponseEntity.ok().body(adminDto);
  }
}
