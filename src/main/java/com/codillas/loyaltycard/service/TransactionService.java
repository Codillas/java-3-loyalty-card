package com.codillas.loyaltycard.service;

import com.codillas.loyaltycard.service.model.Direction;
import com.codillas.loyaltycard.service.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    Transaction createTransaction(UUID cardId, UUID adminId, Direction direction, int amount, String note);

    List<Transaction> getTransactions(UUID cardId);

    Transaction getTransaction(UUID cardId, UUID transactionId);

    Transaction cancelTransaction(UUID cardId, UUID transactionId);

}
