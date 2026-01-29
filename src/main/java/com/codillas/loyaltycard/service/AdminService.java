package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.service.model.Admin;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    Admin createAdmin(String name, String phoneNumber, String email, String password);

    List<Admin> getAdmins();

    Admin getAdmin(UUID adminId);

    Admin activateAdmin(UUID adminId);

    Admin blockAdmin(UUID adminId);

}
