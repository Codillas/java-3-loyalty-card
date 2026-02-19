package com.codillas.loyaltycard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codillas.loyaltycard.controller.dto.DirectionDto;
import com.codillas.loyaltycard.controller.dto.TransactionDto;
import com.codillas.loyaltycard.controller.dto.TransactionStatusDto;
import com.codillas.loyaltycard.mapper.TransactionMapper;
import com.codillas.loyaltycard.repository.AdminRepository;
import com.codillas.loyaltycard.repository.CardRepository;
import com.codillas.loyaltycard.repository.CustomerRepository;
import com.codillas.loyaltycard.repository.TransactionRepository;
import com.codillas.loyaltycard.service.TransactionService;
import com.codillas.loyaltycard.service.model.Direction;
import com.codillas.loyaltycard.service.model.Transaction;
import com.codillas.loyaltycard.service.model.TransactionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class TransactionControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private TransactionMapper transactionMapper;

    @MockitoBean
    private TransactionRepository transactionRepository;
    @MockitoBean
    private CardRepository cardRepository;
    @MockitoBean
    private CustomerRepository customerRepository;
    @MockitoBean
    private AdminRepository adminRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // ─── POST /cards/{cardId}/transactions ───────────────────────────────────

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001", authorities = "ADMIN")
    void createTransaction_shouldReturn201WithTransactionDto() throws Exception {
        UUID cardId = UUID.randomUUID();
        UUID adminId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        TransactionDto requestDto = buildTransactionDto(cardId, adminId, DirectionDto.IN, 50);
        Transaction transaction = buildTransaction(cardId, adminId, Direction.IN, 50);
        TransactionDto responseDto = buildTransactionDto(cardId, adminId, DirectionDto.IN, 50);

        when(transactionService.createTransaction(eq(cardId), eq(adminId), any(Direction.class), eq(50), any()))
                .thenReturn(transaction);
        when(transactionMapper.toDto(transaction)).thenReturn(responseDto);

        mockMvc.perform(post("/cards/{cardId}/transactions", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50))
                .andExpect(jsonPath("$.direction").value("IN"));
    }

    @Test
    void createTransaction_shouldReturn401_whenNotAuthenticated() throws Exception {
        UUID cardId = UUID.randomUUID();
        TransactionDto requestDto = buildTransactionDto(cardId, UUID.randomUUID(), DirectionDto.IN, 50);

        mockMvc.perform(post("/cards/{cardId}/transactions", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void createTransaction_shouldReturn403_whenUserIsNotAdmin() throws Exception {
        UUID cardId = UUID.randomUUID();
        TransactionDto requestDto = buildTransactionDto(cardId, UUID.randomUUID(), DirectionDto.IN, 50);

        mockMvc.perform(post("/cards/{cardId}/transactions", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    // ─── GET /cards/{cardId}/transactions ────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getTransactions_shouldReturn200WithListOfTransactions() throws Exception {
        UUID cardId = UUID.randomUUID();
        Transaction tx = buildTransaction(cardId, UUID.randomUUID(), Direction.IN, 100);
        TransactionDto txDto = buildTransactionDto(cardId, UUID.randomUUID(), DirectionDto.IN, 100);

        when(transactionService.getTransactions(cardId)).thenReturn(List.of(tx));
        when(transactionMapper.toDto(tx)).thenReturn(txDto);

        mockMvc.perform(get("/cards/{cardId}/transactions", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100));
    }

    @Test
    void getTransactions_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/cards/{cardId}/transactions", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    // ─── GET /cards/{cardId}/transactions/{transactionId} ────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getTransaction_shouldReturn200WithTransactionDto() throws Exception {
        UUID cardId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        Transaction tx = buildTransaction(cardId, UUID.randomUUID(), Direction.OUT, 30);
        TransactionDto txDto = buildTransactionDto(cardId, UUID.randomUUID(), DirectionDto.OUT, 30);

        when(transactionService.getTransaction(cardId, txId)).thenReturn(tx);
        when(transactionMapper.toDto(tx)).thenReturn(txDto);

        mockMvc.perform(get("/cards/{cardId}/transactions/{txId}", cardId, txId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direction").value("OUT"));
    }

    @Test
    void getTransaction_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/cards/{cardId}/transactions/{txId}", UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    // ─── PUT /cards/{cardId}/transactions/{transactionId}/cancel ─────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void cancelTransaction_shouldReturn200WithCancelledTransactionDto() throws Exception {
        UUID cardId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        Transaction tx = buildTransaction(cardId, UUID.randomUUID(), Direction.IN, 50);
        TransactionDto txDto = buildTransactionDto(cardId, UUID.randomUUID(), DirectionDto.IN, 50);

        when(transactionService.cancelTransaction(cardId, txId)).thenReturn(tx);
        when(transactionMapper.toDto(tx)).thenReturn(txDto);

        mockMvc.perform(put("/cards/{cardId}/transactions/{txId}/cancel", cardId, txId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(50));
    }

    @Test
    void cancelTransaction_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/cards/{cardId}/transactions/{txId}/cancel",
                UUID.randomUUID(), UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Transaction buildTransaction(UUID cardId, UUID adminId, Direction direction, int amount) {
        return new Transaction(UUID.randomUUID(), cardId, adminId, direction, amount,
                TransactionStatus.SUCCESS, "note", Instant.now(), Instant.now());
    }

    private TransactionDto buildTransactionDto(UUID cardId, UUID adminId, DirectionDto direction, int amount) {
        return new TransactionDto(UUID.randomUUID(), cardId, adminId, direction, amount,
                TransactionStatusDto.SUCCESS, "note", null, null);
    }
}
