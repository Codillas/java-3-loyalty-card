package com.codillas.loyaltycard.controller;

import com.codillas.loyaltycard.controller.dto.TransactionDto;
import com.codillas.loyaltycard.mapper.TransactionMapper;
import com.codillas.loyaltycard.service.TransactionService;
import com.codillas.loyaltycard.service.model.Direction;
import com.codillas.loyaltycard.service.model.Transaction;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards/{cardId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @Secured("ADMIN")
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(
            @PathVariable UUID cardId,
            @RequestBody TransactionDto transactionDto,
            Authentication authentication) {

        UUID adminId = UUID.fromString(authentication.getName());

        Transaction transaction = transactionService.createTransaction(
                cardId,
                adminId,
                Direction.valueOf(transactionDto.getDirection().name()),
                transactionDto.getAmount(),
                transactionDto.getNote());

        TransactionDto responseDto = transactionMapper.toDto(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Secured("ADMIN")
    @GetMapping
    public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable UUID cardId) {

        List<Transaction> transactions = transactionService.getTransactions(cardId);

        List<TransactionDto> transactionDtos = transactions.stream()
                .map(transactionMapper::toDto)
                .toList();

        return ResponseEntity.ok().body(transactionDtos);
    }

    @Secured("ADMIN")
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransaction(
            @PathVariable UUID cardId,
            @PathVariable UUID transactionId) {

        Transaction transaction = transactionService.getTransaction(cardId, transactionId);

        TransactionDto transactionDto = transactionMapper.toDto(transaction);

        return ResponseEntity.ok().body(transactionDto);
    }

    @Secured("ADMIN")
    @PutMapping("/{transactionId}/cancel")
    public ResponseEntity<TransactionDto> cancelTransaction(
            @PathVariable UUID cardId,
            @PathVariable UUID transactionId) {

        Transaction transaction = transactionService.cancelTransaction(cardId, transactionId);

        TransactionDto transactionDto = transactionMapper.toDto(transaction);

        return ResponseEntity.ok().body(transactionDto);
    }
}
