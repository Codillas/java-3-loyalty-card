package com.codillas.loyaltycard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codillas.loyaltycard.exception.CardIsBlockedException;
import com.codillas.loyaltycard.exception.CardNotFoundException;
import com.codillas.loyaltycard.exception.TransactionNotFoundException;
import com.codillas.loyaltycard.mapper.TransactionMapper;
import com.codillas.loyaltycard.repository.CardRepository;
import com.codillas.loyaltycard.repository.TransactionRepository;
import com.codillas.loyaltycard.repository.entity.CardEntity;
import com.codillas.loyaltycard.repository.entity.CustomerEntity;
import com.codillas.loyaltycard.repository.entity.Direction;
import com.codillas.loyaltycard.repository.entity.Status;
import com.codillas.loyaltycard.repository.entity.TransactionEntity;
import com.codillas.loyaltycard.repository.entity.TransactionStatus;
import com.codillas.loyaltycard.service.model.Transaction;
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
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // ─── createTransaction ────────────────────────────────────────────────────

    @Test
    void createTransaction_shouldAddAmountToBalance_whenDirectionIsIn() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.ACTIVE, 100);

        TransactionEntity savedTx = buildTransactionEntity(cardId, adminId, Direction.IN, 50);
        Transaction expected = buildTransaction(cardId, adminId,
                com.codillas.loyaltycard.service.model.Direction.IN, 50);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedTx);
        when(transactionMapper.toDomain(savedTx)).thenReturn(expected);

        // when
        Transaction result = transactionService.createTransaction(
                cardId, adminId, com.codillas.loyaltycard.service.model.Direction.IN, 50, "deposit");

        // then
        assertThat(cardEntity.getBalance()).isEqualTo(150);
        assertThat(result).isEqualTo(expected);
        verify(cardRepository).save(cardEntity);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void createTransaction_shouldSubtractAmountFromBalance_whenDirectionIsOut() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.ACTIVE, 200);

        TransactionEntity savedTx = buildTransactionEntity(cardId, adminId, Direction.OUT, 80);
        Transaction expected = buildTransaction(cardId, adminId,
                com.codillas.loyaltycard.service.model.Direction.OUT, 80);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedTx);
        when(transactionMapper.toDomain(savedTx)).thenReturn(expected);

        // when
        transactionService.createTransaction(
                cardId, adminId, com.codillas.loyaltycard.service.model.Direction.OUT, 80, "withdraw");

        // then
        assertThat(cardEntity.getBalance()).isEqualTo(120);
    }

    @Test
    void createTransaction_shouldThrowCardIsBlockedException_whenCardIsBlocked() {
        // given
        UUID cardId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.BLOCKED, 100);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));

        // when / then
        assertThatThrownBy(() -> transactionService.createTransaction(
                cardId, UUID.randomUUID(),
                com.codillas.loyaltycard.service.model.Direction.IN, 50, "test"))
                .isInstanceOf(CardIsBlockedException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_shouldThrowCardNotFoundException_whenCardDoesNotExist() {
        // given
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> transactionService.createTransaction(
                cardId, UUID.randomUUID(),
                com.codillas.loyaltycard.service.model.Direction.IN, 10, "note"))
                .isInstanceOf(CardNotFoundException.class);
    }

    // ─── getTransactions ──────────────────────────────────────────────────────

    @Test
    void getTransactions_shouldReturnAllTransactionsForCard() {
        // given
        UUID cardId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.ACTIVE, 0);
        TransactionEntity tx1 = buildTransactionEntity(cardId, UUID.randomUUID(), Direction.IN, 10);
        TransactionEntity tx2 = buildTransactionEntity(cardId, UUID.randomUUID(), Direction.OUT, 5);
        Transaction t1 = buildTransaction(cardId, UUID.randomUUID(),
                com.codillas.loyaltycard.service.model.Direction.IN, 10);
        Transaction t2 = buildTransaction(cardId, UUID.randomUUID(),
                com.codillas.loyaltycard.service.model.Direction.OUT, 5);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(transactionRepository.findAllByCardId(cardId)).thenReturn(List.of(tx1, tx2));
        when(transactionMapper.toDomain(tx1)).thenReturn(t1);
        when(transactionMapper.toDomain(tx2)).thenReturn(t2);

        // when
        List<Transaction> result = transactionService.getTransactions(cardId);

        // then
        assertThat(result).containsExactly(t1, t2);
    }

    @Test
    void getTransactions_shouldThrowCardNotFoundException_whenCardDoesNotExist() {
        // given
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> transactionService.getTransactions(cardId))
                .isInstanceOf(CardNotFoundException.class);
    }

    // ─── getTransaction ───────────────────────────────────────────────────────

    @Test
    void getTransaction_shouldReturnTransaction_whenBothExist() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.ACTIVE, 0);
        TransactionEntity txEntity = buildTransactionEntity(cardId, UUID.randomUUID(), Direction.IN, 20);
        Transaction expected = buildTransaction(cardId, UUID.randomUUID(),
                com.codillas.loyaltycard.service.model.Direction.IN, 20);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(txEntity));
        when(transactionMapper.toDomain(txEntity)).thenReturn(expected);

        // when
        Transaction result = transactionService.getTransaction(cardId, txId);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getTransaction_shouldThrowTransactionNotFoundException_whenTransactionDoesNotExist() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.ACTIVE, 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(transactionRepository.findById(txId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> transactionService.getTransaction(cardId, txId))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    // ─── cancelTransaction ────────────────────────────────────────────────────

    @Test
    void cancelTransaction_shouldReverseInBalance_whenDirectionWasIn() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.ACTIVE, 150);
        TransactionEntity txEntity = buildTransactionEntity(cardId, UUID.randomUUID(), Direction.IN, 50);
        txEntity.setId(txId);
        TransactionEntity savedTx = buildTransactionEntity(cardId, UUID.randomUUID(), Direction.IN, 50);
        Transaction expected = buildTransaction(cardId, UUID.randomUUID(),
                com.codillas.loyaltycard.service.model.Direction.IN, 50);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(txEntity));
        when(transactionRepository.save(txEntity)).thenReturn(savedTx);
        when(transactionMapper.toDomain(savedTx)).thenReturn(expected);

        // when
        transactionService.cancelTransaction(cardId, txId);

        // then – balance reduced by 50 (reversing the IN)
        assertThat(cardEntity.getBalance()).isEqualTo(100);
        assertThat(txEntity.getStatus()).isEqualTo(TransactionStatus.CANCELLED);
    }

    @Test
    void cancelTransaction_shouldReverseOutBalance_whenDirectionWasOut() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.ACTIVE, 50);
        TransactionEntity txEntity = buildTransactionEntity(cardId, UUID.randomUUID(), Direction.OUT, 50);
        txEntity.setId(txId);
        TransactionEntity savedTx = buildTransactionEntity(cardId, UUID.randomUUID(), Direction.OUT, 50);
        Transaction expected = buildTransaction(cardId, UUID.randomUUID(),
                com.codillas.loyaltycard.service.model.Direction.OUT, 50);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(txEntity));
        when(transactionRepository.save(txEntity)).thenReturn(savedTx);
        when(transactionMapper.toDomain(savedTx)).thenReturn(expected);

        // when
        transactionService.cancelTransaction(cardId, txId);

        // then – balance increased by 50 (reversing the OUT)
        assertThat(cardEntity.getBalance()).isEqualTo(100);
    }

    @Test
    void cancelTransaction_shouldThrowCardNotFoundException_whenCardDoesNotExist() {
        // given
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> transactionService.cancelTransaction(cardId, UUID.randomUUID()))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void cancelTransaction_shouldThrowTransactionNotFoundException_whenTransactionDoesNotExist() {
        // given
        UUID cardId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        CardEntity cardEntity = buildCardEntity(cardId, Status.ACTIVE, 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(transactionRepository.findById(txId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> transactionService.cancelTransaction(cardId, txId))
                .isInstanceOf(TransactionNotFoundException.class);
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

    private TransactionEntity buildTransactionEntity(UUID cardId, UUID adminId, Direction direction, int amount) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(UUID.randomUUID());
        entity.setCardId(cardId);
        entity.setAdminId(adminId);
        entity.setDirection(direction);
        entity.setAmount(amount);
        entity.setStatus(TransactionStatus.SUCCESS);
        entity.setNote("note");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    private Transaction buildTransaction(UUID cardId, UUID adminId,
            com.codillas.loyaltycard.service.model.Direction direction, int amount) {
        return new Transaction(UUID.randomUUID(), cardId, adminId, direction, amount,
                com.codillas.loyaltycard.service.model.TransactionStatus.SUCCESS, "note",
                Instant.now(), Instant.now());
    }
}
