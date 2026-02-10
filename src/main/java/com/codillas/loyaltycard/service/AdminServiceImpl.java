package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.exception.AdminAlreadyExistsException;
import com.codillas.loyaltycard.exception.AdminNotFoundException;
import com.codillas.loyaltycard.mapper.AdminMapper;
import com.codillas.loyaltycard.repository.AdminRepository;
import com.codillas.loyaltycard.repository.entity.AdminEntity;
import com.codillas.loyaltycard.service.model.Admin;
import com.codillas.loyaltycard.service.model.Status;
import com.codillas.loyaltycard.service.model.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

  private AdminRepository adminRepository;
  private AdminMapper adminMapper;

  @Autowired
  public AdminServiceImpl(AdminRepository adminRepository,  AdminMapper adminMapper) {
    this.adminRepository = adminRepository;
    this.adminMapper = adminMapper;
  }

  private HashMap<UUID, Admin> adminHashMap = new HashMap<>();

  @Override
  public Admin createAdmin(String name, String phoneNumber, String email, String password) {

    log.info("Attempting to create an admin with email {}", email);

    Optional<AdminEntity> optionalAdmin = adminRepository.findByEmail(email);


    if (optionalAdmin.isPresent()){
      throw new AdminAlreadyExistsException(email);
    }

    AdminEntity adminEntity = new AdminEntity();
    adminEntity.setName(name);
    adminEntity.setEmail(email);
    adminEntity.setPhoneNumber(phoneNumber);
    adminEntity.setPassword(password);

    Instant now = Instant.now();

    adminEntity.setCreatedAt(now);
    adminEntity.setUpdatedAt(now);
    adminEntity.setType(com.codillas.loyaltycard.repository.entity.Type.ADMIN);
    adminEntity.setStatus(com.codillas.loyaltycard.repository.entity.Status.ACTIVE);

    AdminEntity savedAdminEntity = adminRepository.save(adminEntity);

    log.info("Successfully created an admin with email {}", email);

    return adminMapper.toDomain(savedAdminEntity);
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
