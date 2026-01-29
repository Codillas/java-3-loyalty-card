package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.AdminDto;
import com.codillas.loyaltycard.controller.dto.SignUpRequestDto;
import com.codillas.loyaltycard.mapper.AdminMapper;
import com.codillas.loyaltycard.service.AdminService;
import com.codillas.loyaltycard.service.model.Admin;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {

  private final AdminService adminService;
  private final AdminMapper adminMapper;

  @Autowired
  public AdminController(AdminService adminService, AdminMapper adminMapper) {
    this.adminService = adminService;
    this.adminMapper = adminMapper;
  }

  @PostMapping("/admins")
  public ResponseEntity<AdminDto> createAdmin(@RequestBody SignUpRequestDto signUpRequestDto) {

    Admin admin =
        adminService.createAdmin(
            signUpRequestDto.getName(),
            signUpRequestDto.getPhoneNumber(),
            signUpRequestDto.getEmail(),
            signUpRequestDto.getPassword());

    AdminDto adminDto = adminMapper.toDto(admin);

    return ResponseEntity.ok().body(adminDto);
  }

  @GetMapping("/admins")
  public ResponseEntity<List<AdminDto>> getAdmins() {

    List<Admin> adminList = adminService.getAdmins();

    List<AdminDto> adminDtoList = adminList.stream().map(adminMapper::toDto).toList();

    return ResponseEntity.ok().body(adminDtoList);
  }

  @GetMapping("/admins/{adminId}")
  public ResponseEntity<AdminDto> getAdmin(@PathVariable UUID adminId) {

    Admin admin = adminService.getAdmin(adminId);

    AdminDto adminDto = adminMapper.toDto(admin);

    return ResponseEntity.ok().body(adminDto);
  }

  @PutMapping("/admins/{adminId}/activate")
  public ResponseEntity<AdminDto> activateAdmin(@PathVariable UUID adminId) {

    Admin admin = adminService.activateAdmin(adminId);

    AdminDto adminDto = adminMapper.toDto(admin);

    return ResponseEntity.ok().body(adminDto);
  }

  @PutMapping("/admins/{adminId}/block")
  public ResponseEntity<AdminDto> blockAdmin(@PathVariable UUID adminId) {

    Admin admin = adminService.blockAdmin(adminId);

    AdminDto adminDto = adminMapper.toDto(admin);

    return ResponseEntity.ok().body(adminDto);
  }
}
