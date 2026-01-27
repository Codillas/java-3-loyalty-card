package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.AdminDto;
import com.codillas.loyaltycard.controller.dto.SignUpRequestDto;
import com.codillas.loyaltycard.controller.dto.Status;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.codillas.loyaltycard.controller.dto.Type;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {

  private HashMap<UUID, AdminDto> adminDtoHashMap = new HashMap<>();

  @PostMapping("/admins")
  public ResponseEntity<AdminDto> createAdmin(@RequestBody SignUpRequestDto signUpRequestDto) {

    AdminDto adminDto = new AdminDto();
    adminDto.setName(signUpRequestDto.getName());
    adminDto.setEmail(signUpRequestDto.getEmail());
    adminDto.setPhoneNumber(signUpRequestDto.getPhoneNumber());

    Instant now = Instant.now();

    adminDto.setCreatedAt(now);
    adminDto.setUpdatedAt(now);
    adminDto.setType(Type.ADMIN);
    adminDto.setStatus(Status.ACTIVE);
    adminDto.setId(UUID.randomUUID());

    adminDtoHashMap.put(adminDto.getId(), adminDto);

    return ResponseEntity.ok().body(adminDto);
  }

  @GetMapping("/admins")
  public ResponseEntity<List<AdminDto>> getAdmins() {

    List<AdminDto> adminDtos = adminDtoHashMap.values().stream().toList();

    return ResponseEntity.ok().body(adminDtos);
  }

  @GetMapping("/admins/{adminId}")
  public ResponseEntity<AdminDto> getAdmin(@PathVariable UUID adminId) {

    AdminDto adminDto = adminDtoHashMap.get(adminId);

    return ResponseEntity.ok().body(adminDto);
  }

  @PutMapping("/admins/{adminId}/activate")
  public ResponseEntity<AdminDto> activateAdmin(@PathVariable UUID adminId){

    AdminDto adminDto = adminDtoHashMap.get(adminId);

    adminDto.setStatus(Status.ACTIVE);
    adminDto.setUpdatedAt(Instant.now());
    adminDtoHashMap.put(adminDto.getId(), adminDto);

    return ResponseEntity.ok().body(adminDto);
  }

  @PutMapping("/admins/{adminId}/block")
  public ResponseEntity<AdminDto> blockAdmin(@PathVariable UUID adminId){

    AdminDto adminDto = adminDtoHashMap.get(adminId);

    adminDto.setStatus(Status.BLOCKED);
    adminDto.setUpdatedAt(Instant.now());
    adminDtoHashMap.put(adminDto.getId(), adminDto);

    return ResponseEntity.ok().body(adminDto);
  }

}
