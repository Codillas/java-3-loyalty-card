package com.codillas.loyaltycard.service;

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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final CardMapper cardMapper;

    @Override
    public Card createCard(UUID customerId) {

        log.info("Creating card for customer {}", customerId);

        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        CardEntity cardEntity = new CardEntity();
        cardEntity.setCustomer(customerEntity);
        cardEntity.setBalance(0);
        cardEntity.setStatus(Status.ACTIVE);

        Instant now = Instant.now();
        cardEntity.setCreatedAt(now);
        cardEntity.setUpdatedAt(now);

        CardEntity savedEntity = cardRepository.save(cardEntity);

        log.info("Successfully created card {} for customer {}", savedEntity.getId(), customerId);

        return cardMapper.toDomain(savedEntity);
    }

    @Override
    public Card getCard(UUID cardId) {
        return cardRepository.findById(cardId)
                .map(cardMapper::toDomain)
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }

    @Override
    public Card activateCard(UUID cardId) {
        CardEntity cardEntity = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        cardEntity.setStatus(Status.ACTIVE);
        cardEntity.setUpdatedAt(Instant.now());

        CardEntity savedEntity = cardRepository.save(cardEntity);

        return cardMapper.toDomain(savedEntity);
    }

    @Override
    public Card blockCard(UUID cardId) {
        CardEntity cardEntity = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        cardEntity.setStatus(Status.BLOCKED);
        cardEntity.setUpdatedAt(Instant.now());

        CardEntity savedEntity = cardRepository.save(cardEntity);

        return cardMapper.toDomain(savedEntity);
    }
}
