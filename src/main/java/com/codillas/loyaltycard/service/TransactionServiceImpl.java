package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.exception.CardIsBlockedException;
import com.codillas.loyaltycard.exception.CardNotFoundException;
import com.codillas.loyaltycard.exception.TransactionNotFoundException;
import com.codillas.loyaltycard.mapper.TransactionMapper;
import com.codillas.loyaltycard.repository.CardRepository;
import com.codillas.loyaltycard.repository.TransactionRepository;
import com.codillas.loyaltycard.repository.entity.CardEntity;
import com.codillas.loyaltycard.repository.entity.Status;
import com.codillas.loyaltycard.repository.entity.TransactionEntity;
import com.codillas.loyaltycard.repository.entity.TransactionStatus;
import com.codillas.loyaltycard.service.model.Direction;
import com.codillas.loyaltycard.service.model.Transaction;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public Transaction createTransaction(UUID cardId, UUID adminId, Direction direction, int amount, String note) {

        log.info("Creating transaction for card {} by admin {}", cardId, adminId);

        CardEntity cardEntity = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (cardEntity.getStatus() == Status.BLOCKED) {
            throw new CardIsBlockedException();
        }

        // Update card balance
        if (direction == Direction.IN) {
            cardEntity.setBalance(cardEntity.getBalance() + amount);
        } else {
            cardEntity.setBalance(cardEntity.getBalance() - amount);
        }
        cardEntity.setUpdatedAt(Instant.now());
        cardRepository.save(cardEntity);

        // Create transaction
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setCardId(cardId);
        transactionEntity.setAdminId(adminId);
        transactionEntity.setDirection(com.codillas.loyaltycard.repository.entity.Direction.valueOf(direction.name()));
        transactionEntity.setAmount(amount);
        transactionEntity.setStatus(TransactionStatus.SUCCESS);
        transactionEntity.setNote(note);

        Instant now = Instant.now();
        transactionEntity.setCreatedAt(now);
        transactionEntity.setUpdatedAt(now);

        TransactionEntity savedEntity = transactionRepository.save(transactionEntity);

        log.info("Successfully created transaction {} for card {}", savedEntity.getId(), cardId);

        return transactionMapper.toDomain(savedEntity);
    }

    @Override
    public List<Transaction> getTransactions(UUID cardId) {

        // Verify card exists
        cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        return transactionRepository.findAllByCardId(cardId).stream()
                .map(transactionMapper::toDomain)
                .toList();
    }

    @Override
    public Transaction getTransaction(UUID cardId, UUID transactionId) {

        // Verify card exists
        cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        TransactionEntity transactionEntity = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        return transactionMapper.toDomain(transactionEntity);
    }

    @Override
    @Transactional
    public Transaction cancelTransaction(UUID cardId, UUID transactionId) {

        log.info("Cancelling transaction {} for card {}", transactionId, cardId);

        CardEntity cardEntity = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        TransactionEntity transactionEntity = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        // Reverse the balance change
        if (transactionEntity.getDirection() == com.codillas.loyaltycard.repository.entity.Direction.IN) {
            cardEntity.setBalance(cardEntity.getBalance() - transactionEntity.getAmount());
        } else {
            cardEntity.setBalance(cardEntity.getBalance() + transactionEntity.getAmount());
        }
        cardEntity.setUpdatedAt(Instant.now());
        cardRepository.save(cardEntity);

        // Update transaction status
        transactionEntity.setStatus(TransactionStatus.CANCELLED);
        transactionEntity.setUpdatedAt(Instant.now());
        TransactionEntity savedEntity = transactionRepository.save(transactionEntity);

        log.info("Successfully cancelled transaction {} for card {}", transactionId, cardId);

        return transactionMapper.toDomain(savedEntity);
    }
}
