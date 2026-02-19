package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.exception.AdminIsBlockedException;
import com.codillas.loyaltycard.exception.CustomerIsBlockedException;
import com.codillas.loyaltycard.exception.InvalidCredentialsException;
import com.codillas.loyaltycard.service.model.Admin;
import com.codillas.loyaltycard.service.model.Customer;
import com.codillas.loyaltycard.service.model.Status;
import com.codillas.loyaltycard.service.model.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final CustomerService customerService;
  private final AdminService adminService;
  private final CardService cardService;
  private final BCryptPasswordEncoder bCryptpasswordEncoder;
  private final TokenService tokenService;

  @Override
  public String signUp(String name, String phoneNumber, String email, String password) {

    Customer customer = customerService.createCustomer(
        name, phoneNumber, email, bCryptpasswordEncoder.encode(password));

    // Auto-create a loyalty card for the new customer
    cardService.createCard(customer.getId());

    String token = tokenService.createToken(customer.getId().toString(), Type.CUSTOMER);

    return token;
  }

  @Override
  public String loginCustomer(String email, String password) {

    Customer customer = customerService.getCustomer(email);

    boolean matches = bCryptpasswordEncoder.matches(password, customer.getPassword());

    if (!matches) {
      throw new InvalidCredentialsException();
    }

    if (customer.getStatus() == Status.BLOCKED) {
      throw new CustomerIsBlockedException();
    }

    String token = tokenService.createToken(customer.getId().toString(), Type.CUSTOMER);

    return token;
  }

  @Override
  public String loginAdmin(String email, String password) {
    Admin admin = adminService.getAdmin(email);

    boolean matches = bCryptpasswordEncoder.matches(password, admin.getPassword());

    if (!matches) {
      throw new InvalidCredentialsException();
    }

    if (admin.getStatus() == Status.BLOCKED) {
      throw new AdminIsBlockedException();
    }

    String token = tokenService.createToken(admin.getId().toString(), admin.getType());

    return token;
  }
}
