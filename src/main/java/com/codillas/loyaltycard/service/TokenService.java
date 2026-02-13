package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.service.model.Type;

public interface TokenService {

  String createToken(String id, Type type);

  boolean isValidToken(String token);

  String getId(String token);

  Type getType(String token);
}
