package com.codillas.loyaltycard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codillas.loyaltycard.exception.CardNotFoundException;
import com.codillas.loyaltycard.exception.CustomerNotFoundException;
import com.codillas.loyaltycard.mapper.CardMapper;
import com.codillas.loyaltycard.repository.CardRepository;
import com.codillas.loyaltycard.repository.CustomerRepository;
import com.codillas.loyaltycard.repository.entity.CardEntity;
import com.codillas.loyaltycard.repository.entity.CustomerEntity;
import com.codillas.loyaltycard.repository.entity.Status;
import com.codillas.loyaltycard.service.model.Card;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    // ─── createCard ──────────────────────────────────────────────────────────

    @Test
    void createCard_shouldCreateAndReturnCard_whenCustomerExists() {
        // given
        UUID customerId = UUID.randomUUID();

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        CardEntity savedEntity = new CardEntity();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setCustomer(customerEntity);
        savedEntity.setBalance(0);
        savedEntity.setStatus(Status.ACTIVE);
        savedEntity.setCreatedAt(Instant.now());
        savedEntity.setUpdatedAt(Instant.now());

        Card expectedCard = new Card(savedEntity.getId(), customerId, 0,
                com.codillas.loyaltycard.service.model.Status.ACTIVE,
                savedEntity.getCreatedAt(), savedEntity.getUpdatedAt());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        when(cardRepository.save(any(CardEntity.class))).thenReturn(savedEntity);
        when(cardMapper.toDomain(savedEntity)).thenReturn(expectedCard);

        // when
        Card result = cardService.createCard(customerId);

        // then
        assertThat(result).isEqualTo(expectedCard);
        verify(customerRepository).findById(customerId);
        verify(cardRepository).save(any(CardEntity.class));
        verify(cardMapper).toDomain(savedEntity);
    }

    @Test
    void createCard_shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        // given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> cardService.createCard(customerId))
                .isInstanceOf(CustomerNotFoundException.class);

        verify(cardRepository, never()).save(any());
    }

    // ─── getCard ─────────────────────────────────────────────────────────────

    @Test
    void getCard_shouldReturnCard_whenCardExists() {
        // given
        UUID cardId = UUID.randomUUID();
        CardEntity entity = buildCardEntity(cardId, Status.ACTIVE, 100);
        Card expected = buildCard(cardId, com.codillas.loyaltycard.service.model.Status.ACTIVE, 100);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(entity));
        when(cardMapper.toDomain(entity)).thenReturn(expected);

        // when
        Card result = cardService.getCard(cardId);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getCard_shouldThrowCardNotFoundException_whenCardDoesNotExist() {
        // given
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> cardService.getCard(cardId))
                .isInstanceOf(CardNotFoundException.class);
    }

    // ─── activateCard ─────────────────────────────────────────────────────────

    @Test
    void activateCard_shouldSetStatusActiveAndSave() {
        // given
        UUID cardId = UUID.randomUUID();
        CardEntity entity = buildCardEntity(cardId, Status.BLOCKED, 0);
        CardEntity savedEntity = buildCardEntity(cardId, Status.ACTIVE, 0);
        Card expected = buildCard(cardId, com.codillas.loyaltycard.service.model.Status.ACTIVE, 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(entity));
        when(cardRepository.save(entity)).thenReturn(savedEntity);
        when(cardMapper.toDomain(savedEntity)).thenReturn(expected);

        // when
        Card result = cardService.activateCard(cardId);

        // then
        assertThat(result.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.Status.ACTIVE);
        verify(cardRepository).save(entity);
    }

    @Test
    void activateCard_shouldThrowCardNotFoundException_whenCardDoesNotExist() {
        // given
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> cardService.activateCard(cardId))
                .isInstanceOf(CardNotFoundException.class);
    }

    // ─── blockCard ───────────────────────────────────────────────────────────

    @Test
    void blockCard_shouldSetStatusBlockedAndSave() {
        // given
        UUID cardId = UUID.randomUUID();
        CardEntity entity = buildCardEntity(cardId, Status.ACTIVE, 50);
        CardEntity savedEntity = buildCardEntity(cardId, Status.BLOCKED, 50);
        Card expected = buildCard(cardId, com.codillas.loyaltycard.service.model.Status.BLOCKED, 50);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(entity));
        when(cardRepository.save(entity)).thenReturn(savedEntity);
        when(cardMapper.toDomain(savedEntity)).thenReturn(expected);

        // when
        Card result = cardService.blockCard(cardId);

        // then
        assertThat(result.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.Status.BLOCKED);
        verify(cardRepository).save(entity);
    }

    @Test
    void blockCard_shouldThrowCardNotFoundException_whenCardDoesNotExist() {
        // given
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> cardService.blockCard(cardId))
                .isInstanceOf(CardNotFoundException.class);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private CardEntity buildCardEntity(UUID cardId, Status status, int balance) {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(UUID.randomUUID());

        CardEntity entity = new CardEntity();
        entity.setId(cardId);
        entity.setCustomer(customer);
        entity.setBalance(balance);
        entity.setStatus(status);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    private Card buildCard(UUID cardId, com.codillas.loyaltycard.service.model.Status status, int balance) {
        return new Card(cardId, UUID.randomUUID(), balance, status, Instant.now(), Instant.now());
    }
}
