package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.exception.AdminAlreadyExistsException;
import com.codillas.loyaltycard.exception.AdminNotFoundException;
import com.codillas.loyaltycard.mapper.AdminMapper;
import com.codillas.loyaltycard.repository.AdminRepository;
import com.codillas.loyaltycard.repository.entity.AdminEntity;
import com.codillas.loyaltycard.repository.entity.Status;
import com.codillas.loyaltycard.repository.entity.Type;
import com.codillas.loyaltycard.service.model.Admin;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final AdminRepository adminRepository;
  private final AdminMapper adminMapper;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public Admin createAdmin(String name, String phoneNumber, String email, String password) {

    log.info("Attempting to create an admin with email {}", email);

    Optional<AdminEntity> optionalAdmin = adminRepository.findByEmailIgnoreCase(email);

    if (optionalAdmin.isPresent()) {
      throw new AdminAlreadyExistsException(email);
    }

    AdminEntity adminEntity = new AdminEntity();
    adminEntity.setName(name);
    adminEntity.setEmail(email);
    adminEntity.setPhoneNumber(phoneNumber);
    adminEntity.setPassword(bCryptPasswordEncoder.encode(password));

    Instant now = Instant.now();

    adminEntity.setCreatedAt(now);
    adminEntity.setUpdatedAt(now);
    adminEntity.setType(Type.ADMIN);
    adminEntity.setStatus(Status.ACTIVE);

    AdminEntity savedAdminEntity = adminRepository.save(adminEntity);

    log.info("Successfully created an admin with email {}", email);

    return adminMapper.toDomain(savedAdminEntity);
  }

  @Override
  public List<Admin> getAdmins() {

    List<AdminEntity> adminEntityList = adminRepository.findAll();

    List<Admin> adminList =
        adminEntityList.stream().map(adminEntity -> adminMapper.toDomain(adminEntity)).toList();

    return adminList;
  }

  @Override
  public Admin getAdmin(UUID adminId) {

    Optional<AdminEntity> optionalAdmin = adminRepository.findById(adminId);

    if (optionalAdmin.isEmpty()) {
      throw new AdminNotFoundException(adminId);
    }

    AdminEntity adminEntity = optionalAdmin.get();
    Admin admin = adminMapper.toDomain(adminEntity);

    return admin;
  }

  @Override
  public Admin getAdmin(String email) {
    Optional<AdminEntity> optionalAdmin = adminRepository.findByEmailIgnoreCase(email);

    if (optionalAdmin.isEmpty()) {
      throw new AdminNotFoundException(email);
    }

    AdminEntity adminEntity = optionalAdmin.get();
    Admin admin = adminMapper.toDomain(adminEntity);

    return admin;
  }

  @Override
  public Admin activateAdmin(UUID adminId) {

    Optional<AdminEntity> optionalAdmin = adminRepository.findById(adminId);

    if (optionalAdmin.isEmpty()) {
      throw new AdminNotFoundException(adminId);
    }

    AdminEntity adminEntity = optionalAdmin.get();
    adminEntity.setStatus(Status.ACTIVE);
    AdminEntity savedAdminEntity = adminRepository.save(adminEntity);

    Admin admin = adminMapper.toDomain(savedAdminEntity);
    return admin;
  }

  @Override
  public Admin blockAdmin(UUID adminId) {
    Optional<AdminEntity> optionalAdmin = adminRepository.findById(adminId);

    if (optionalAdmin.isEmpty()) {
      throw new AdminNotFoundException(adminId);
    }

    AdminEntity adminEntity = optionalAdmin.get();
    adminEntity.setStatus(Status.BLOCKED);
    AdminEntity savedAdminEntity = adminRepository.save(adminEntity);

    Admin admin = adminMapper.toDomain(savedAdminEntity);
    return admin;
  }

  @Override
  public Admin updateAdmin(UUID adminId, String name, String phoneNumber, String email) {
    Optional<AdminEntity> optionalAdmin = adminRepository.findById(adminId);

    if (optionalAdmin.isEmpty()) {
      throw new AdminNotFoundException(adminId);
    }

    AdminEntity adminEntity = optionalAdmin.get();
    adminEntity.setName(name);
    adminEntity.setPhoneNumber(phoneNumber);
    adminEntity.setEmail(email);
    adminEntity.setUpdatedAt(Instant.now());

    AdminEntity savedAdminEntity = adminRepository.save(adminEntity);

    return adminMapper.toDomain(savedAdminEntity);
  }
}
