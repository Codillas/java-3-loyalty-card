package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.exception.AdminAlreadyExistsException;
import com.codillas.loyaltycard.exception.AdminNotFoundException;
import com.codillas.loyaltycard.service.model.Admin;
import com.codillas.loyaltycard.service.model.Status;
import com.codillas.loyaltycard.service.model.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

  private HashMap<UUID, Admin> adminHashMap = new HashMap<>();

  @Override
  public Admin createAdmin(String name, String phoneNumber, String email, String password) {

    log.info("Attempting to create an admin with email {}", email);

    Optional<Admin> optionalAdmin = adminHashMap.values().stream()
            .filter(admin -> admin.getEmail().equals(email))
            .findFirst();

    if (optionalAdmin.isPresent()){
      throw new AdminAlreadyExistsException(email);
    }

    Admin admin = new Admin();
    admin.setName(name);
    admin.setEmail(email);
    admin.setPhoneNumber(phoneNumber);
    admin.setPassword(password);

    Instant now = Instant.now();

    admin.setCreatedAt(now);
    admin.setUpdatedAt(now);
    admin.setType(Type.ADMIN);
    admin.setStatus(Status.ACTIVE);
    admin.setId(UUID.randomUUID());

    adminHashMap.put(admin.getId(), admin);

    log.info("Succsessfully created an admin with email {}", email);

    return admin;
  }

  @Override
  public List<Admin> getAdmins() {

    return adminHashMap.values().stream().toList();
  }

  @Override
  public Admin getAdmin(UUID adminId) {
    Admin admin = adminHashMap.get(adminId);

    if (admin != null) {
      return admin;
    } else {
      throw new AdminNotFoundException(adminId);
    }
  }

  @Override
  public Admin activateAdmin(UUID adminId) {

    Admin admin = adminHashMap.get(adminId);

    admin.setStatus(Status.ACTIVE);
    admin.setUpdatedAt(Instant.now());
    adminHashMap.put(admin.getId(), admin);

    return admin;
  }

  @Override
  public Admin blockAdmin(UUID adminId) {
    Admin admin = adminHashMap.get(adminId);

    admin.setStatus(Status.BLOCKED);
    admin.setUpdatedAt(Instant.now());
    adminHashMap.put(admin.getId(), admin);

    return admin;
  }
}
