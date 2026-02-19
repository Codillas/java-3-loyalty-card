package com.codillas.loyaltycard.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.codillas.loyaltycard.controller.dto.CustomerDto;
import com.codillas.loyaltycard.repository.entity.CardEntity;
import com.codillas.loyaltycard.repository.entity.CustomerEntity;
import com.codillas.loyaltycard.repository.entity.Status;
import com.codillas.loyaltycard.service.model.Customer;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

  private final CustomerMapper customerMapper = new CustomerMapper();

  @Test
  void toDto_shouldMapAllFields() {
    // given
    UUID id = UUID.randomUUID();
    Instant now = Instant.now();

    Customer customer =
        new Customer(
            id,
            "John",
            "1234567890",
            "john@test.com",
            "password123",
            com.codillas.loyaltycard.service.model.Status.ACTIVE,
            150,
            now,
            now);

    // when
    CustomerDto dto = customerMapper.toDto(customer);

    // then
    assertThat(dto.getId()).isEqualTo(id);
    assertThat(dto.getName()).isEqualTo("John");
    assertThat(dto.getPhoneNumber()).isEqualTo("1234567890");
    assertThat(dto.getEmail()).isEqualTo("john@test.com");
    assertThat(dto.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.Status.ACTIVE);
    assertThat(dto.getBalance()).isEqualTo(150);
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void toDto_shouldMapNullBalance() {
    // given
    Customer customer =
        new Customer(
            UUID.randomUUID(),
            "Jane",
            "9876543210",
            "jane@test.com",
            "pass",
            com.codillas.loyaltycard.service.model.Status.ACTIVE,
            null,
            Instant.now(),
            Instant.now());

    // when
    CustomerDto dto = customerMapper.toDto(customer);

    // then
    assertThat(dto.getBalance()).isNull();
  }

  @Test
  void toDomain_shouldMapAllFieldsWithCard() {
    // given
    UUID id = UUID.randomUUID();
    Instant now = Instant.now();

    CardEntity cardEntity = new CardEntity();
    cardEntity.setBalance(200);

    CustomerEntity entity = new CustomerEntity();
    entity.setId(id);
    entity.setName("John");
    entity.setPhoneNumber("1234567890");
    entity.setEmail("john@test.com");
    entity.setPassword("encoded");
    entity.setStatus(Status.ACTIVE);
    entity.setCreatedAt(now);
    entity.setUpdatedAt(now);
    entity.setCard(cardEntity);

    // when
    Customer customer = customerMapper.toDomain(entity);

    // then
    assertThat(customer.getId()).isEqualTo(id);
    assertThat(customer.getName()).isEqualTo("John");
    assertThat(customer.getPhoneNumber()).isEqualTo("1234567890");
    assertThat(customer.getEmail()).isEqualTo("john@test.com");
    assertThat(customer.getPassword()).isEqualTo("encoded");
    assertThat(customer.getStatus())
        .isEqualTo(com.codillas.loyaltycard.service.model.Status.ACTIVE);
    assertThat(customer.getBalance()).isEqualTo(200);
    assertThat(customer.getCreatedAt()).isEqualTo(now);
    assertThat(customer.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void toDomain_shouldMapNullCardToNullBalance() {
    // given
    CustomerEntity entity = new CustomerEntity();
    entity.setId(UUID.randomUUID());
    entity.setName("Jane");
    entity.setPhoneNumber("9876543210");
    entity.setEmail("jane@test.com");
    entity.setPassword("encoded");
    entity.setStatus(Status.ACTIVE);
    entity.setCreatedAt(Instant.now());
    entity.setUpdatedAt(Instant.now());
    entity.setCard(null);

    // when
    Customer customer = customerMapper.toDomain(entity);

    // then
    assertThat(customer.getBalance()).isNull();
  }
}
