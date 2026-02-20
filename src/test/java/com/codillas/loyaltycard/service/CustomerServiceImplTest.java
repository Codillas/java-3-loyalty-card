package com.codillas.loyaltycard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codillas.loyaltycard.exception.CustomerAlreadyExistsException;
import com.codillas.loyaltycard.exception.CustomerNotFoundException;
import com.codillas.loyaltycard.mapper.CustomerMapper;
import com.codillas.loyaltycard.repository.CustomerRepository;
import com.codillas.loyaltycard.repository.entity.CustomerEntity;
import com.codillas.loyaltycard.repository.entity.Status;
import com.codillas.loyaltycard.service.model.Customer;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    // ─── createCustomer ───────────────────────────────────────────────────────

    @Test
    void createCustomer_shouldCreateAndReturnCustomer_whenEmailIsNotTaken() {
        // given
        String email = "john@example.com";
        CustomerEntity savedEntity = buildEntity(email, Status.ACTIVE);
        Customer expectedCustomer = buildCustomer(email);

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(savedEntity);
        when(customerMapper.toDomain(savedEntity)).thenReturn(expectedCustomer);

        // when
        Customer result = customerService.createCustomer("John", "123456789", email, "password");

        // then
        assertThat(result).isEqualTo(expectedCustomer);
        verify(customerRepository).findByEmailIgnoreCase(email);
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    @Test
    void createCustomer_shouldThrowCustomerAlreadyExistsException_whenEmailIsTaken() {
        // given
        String email = "existing@example.com";
        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(buildEntity(email, Status.ACTIVE)));

        // when / then
        assertThatThrownBy(() -> customerService.createCustomer("John", "123", email, "pass"))
                .isInstanceOf(CustomerAlreadyExistsException.class);

        verify(customerRepository, never()).save(any());
    }

    // ─── getCustomers ─────────────────────────────────────────────────────────

    @Test
    void getCustomers_shouldReturnAllMappedCustomers() {
        // given
        CustomerEntity entity1 = buildEntity("a@test.com", Status.ACTIVE);
        CustomerEntity entity2 = buildEntity("b@test.com", Status.BLOCKED);
        Customer customer1 = buildCustomer("a@test.com");
        Customer customer2 = buildCustomer("b@test.com");

        when(customerRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(customerMapper.toDomain(entity1)).thenReturn(customer1);
        when(customerMapper.toDomain(entity2)).thenReturn(customer2);

        // when
        List<Customer> result = customerService.getCustomers();

        // then
        assertThat(result).containsExactly(customer1, customer2);
    }

    @Test
    void getCustomers_shouldReturnEmptyList_whenNoCustomersExist() {
        // given
        when(customerRepository.findAll()).thenReturn(List.of());

        // when
        List<Customer> result = customerService.getCustomers();

        // then
        assertThat(result).isEmpty();
    }

    // ─── getCustomer(UUID) ────────────────────────────────────────────────────

    @Test
    void getCustomerById_shouldReturnCustomer_whenCustomerExists() {
        // given
        UUID customerId = UUID.randomUUID();
        CustomerEntity entity = buildEntity("c@test.com", Status.ACTIVE);
        entity.setId(customerId);
        Customer expected = buildCustomer("c@test.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(customerMapper.toDomain(entity)).thenReturn(expected);

        // when
        Customer result = customerService.getCustomer(customerId);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getCustomerById_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        // given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> customerService.getCustomer(customerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    // ─── getCustomer(String) ──────────────────────────────────────────────────

    @Test
    void getCustomerByEmail_shouldReturnCustomer_whenCustomerExists() {
        // given
        String email = "find@test.com";
        CustomerEntity entity = buildEntity(email, Status.ACTIVE);
        Customer expected = buildCustomer(email);

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(entity));
        when(customerMapper.toDomain(entity)).thenReturn(expected);

        // when
        Customer result = customerService.getCustomer(email);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getCustomerByEmail_shouldThrowCustomerNotFoundException_whenEmailNotFound() {
        // given
        String email = "ghost@test.com";
        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> customerService.getCustomer(email))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    // ─── updateCustomer ───────────────────────────────────────────────────────

    @Test
    void updateCustomer_shouldUpdateFieldsAndReturnCustomer() {
        // given
        UUID customerId = UUID.randomUUID();
        CustomerEntity entity = buildEntity("old@test.com", Status.ACTIVE);
        entity.setId(customerId);
        CustomerEntity savedEntity = buildEntity("new@test.com", Status.ACTIVE);
        Customer expected = buildCustomer("new@test.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(customerRepository.save(entity)).thenReturn(savedEntity);
        when(customerMapper.toDomain(savedEntity)).thenReturn(expected);

        // when
        Customer result = customerService.updateCustomer(customerId, "New Name", "987654321", "new@test.com");

        // then
        assertThat(result).isEqualTo(expected);
        verify(customerRepository).save(entity);
    }

    @Test
    void updateCustomer_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        // given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> customerService.updateCustomer(customerId, "Name", "123", "email@test.com"))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    // ─── activateCustomer ─────────────────────────────────────────────────────

    @Test
    void activateCustomer_shouldSetStatusActiveAndSave() {
        // given
        UUID customerId = UUID.randomUUID();
        CustomerEntity entity = buildEntity("active@test.com", Status.BLOCKED);
        entity.setId(customerId);
        CustomerEntity savedEntity = buildEntity("active@test.com", Status.ACTIVE);
        Customer expected = buildCustomer("active@test.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(customerRepository.save(entity)).thenReturn(savedEntity);
        when(customerMapper.toDomain(savedEntity)).thenReturn(expected);

        // when
        Customer result = customerService.activateCustomer(customerId);

        // then
        assertThat(result).isEqualTo(expected);
        verify(customerRepository).save(entity);
    }

    @Test
    void activateCustomer_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        // given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> customerService.activateCustomer(customerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    // ─── blockCustomer ────────────────────────────────────────────────────────

    @Test
    void blockCustomer_shouldSetStatusBlockedAndSave() {
        // given
        UUID customerId = UUID.randomUUID();
        CustomerEntity entity = buildEntity("block@test.com", Status.ACTIVE);
        entity.setId(customerId);
        CustomerEntity savedEntity = buildEntity("block@test.com", Status.BLOCKED);
        Customer expected = buildCustomer("block@test.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(customerRepository.save(entity)).thenReturn(savedEntity);
        when(customerMapper.toDomain(savedEntity)).thenReturn(expected);

        // when
        Customer result = customerService.blockCustomer(customerId);

        // then
        assertThat(result).isEqualTo(expected);
        verify(customerRepository).save(entity);
    }

    @Test
    void blockCustomer_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        // given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> customerService.blockCustomer(customerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private CustomerEntity buildEntity(String email, Status status) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("John Doe");
        entity.setPhoneNumber("123456789");
        entity.setEmail(email);
        entity.setPassword("hashed");
        entity.setStatus(status);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    private Customer buildCustomer(String email) {
        return new Customer(UUID.randomUUID(), "John Doe", "123456789", email, "hashed",
                com.codillas.loyaltycard.service.model.Status.ACTIVE, null, Instant.now(), Instant.now());
    }
}
