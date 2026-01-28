package com.codillas.loyaltycard.mapper;

import com.codillas.loyaltycard.controller.dto.AdminDto;
import com.codillas.loyaltycard.service.model.Admin;
import com.codillas.loyaltycard.service.model.Status;
import com.codillas.loyaltycard.service.model.Type;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

  public Admin toDomain(AdminDto dto) {

    Admin admin = new Admin();
    admin.setId(dto.getId());
    admin.setName(dto.getName());
    admin.setEmail(dto.getEmail());
    admin.setCreatedAt(dto.getCreatedAt());
    admin.setUpdatedAt(dto.getUpdatedAt());
    admin.setPhoneNumber(dto.getPhoneNumber());
    admin.setType(Type.valueOf(dto.getType().name()));
    admin.setStatus(Status.valueOf(dto.getStatus().name()));

    return admin;
  }

  public AdminDto toDto(Admin admin) {

    AdminDto dto = new AdminDto();
    dto.setId(admin.getId());
    dto.setName(admin.getName());
    dto.setEmail(admin.getEmail());
    dto.setCreatedAt(admin.getCreatedAt());
    dto.setUpdatedAt(admin.getUpdatedAt());
    dto.setPhoneNumber(admin.getPhoneNumber());
    dto.setType(com.codillas.loyaltycard.controller.dto.Type.valueOf(admin.getType().name()));
    dto.setStatus(com.codillas.loyaltycard.controller.dto.Status.valueOf(admin.getStatus().name()));

    return dto;
  }
}
