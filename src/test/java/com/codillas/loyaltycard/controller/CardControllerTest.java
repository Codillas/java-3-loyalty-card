package com.codillas.loyaltycard.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codillas.loyaltycard.controller.dto.CardDto;
import com.codillas.loyaltycard.mapper.CardMapper;
import com.codillas.loyaltycard.repository.AdminRepository;
import com.codillas.loyaltycard.repository.CardRepository;
import com.codillas.loyaltycard.repository.CustomerRepository;
import com.codillas.loyaltycard.repository.TransactionRepository;
import com.codillas.loyaltycard.service.CardService;
import com.codillas.loyaltycard.service.model.Card;
import com.codillas.loyaltycard.service.model.Status;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class CardControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private CardMapper cardMapper;

    // Repository mocks to prevent DB connections
    @MockitoBean
    private CardRepository cardRepository;
    @MockitoBean
    private CustomerRepository customerRepository;
    @MockitoBean
    private AdminRepository adminRepository;
    @MockitoBean
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // ─── GET /cards/{cardId} ─────────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getCard_shouldReturn200WithCardDto_whenCardExists() throws Exception {
        // given
        UUID cardId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant now = Instant.now();

        Card card = new Card(cardId, customerId, 100, Status.ACTIVE, now, now);
        CardDto cardDto = new CardDto(cardId, customerId, 100, Status.ACTIVE, now, now);

        when(cardService.getCard(cardId)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        // when / then
        mockMvc.perform(get("/cards/{cardId}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.balance").value(100))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getCard_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/cards/{cardId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(cardService);
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void getCard_shouldReturn403_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/cards/{cardId}", UUID.randomUUID()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(cardService);
    }

    // ─── PUT /cards/{cardId}/activate ────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void activateCard_shouldReturn200WithActivatedCardDto() throws Exception {
        // given
        UUID cardId = UUID.randomUUID();
        Instant now = Instant.now();
        Card card = new Card(cardId, UUID.randomUUID(), 0, Status.ACTIVE, now, now);
        CardDto cardDto = new CardDto(cardId, UUID.randomUUID(), 0, Status.ACTIVE, now, now);

        when(cardService.activateCard(cardId)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        // when / then
        mockMvc.perform(put("/cards/{cardId}/activate", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void activateCard_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/cards/{cardId}/activate", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    // ─── PUT /cards/{cardId}/block ───────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void blockCard_shouldReturn200WithBlockedCardDto() throws Exception {
        // given
        UUID cardId = UUID.randomUUID();
        Instant now = Instant.now();
        Card card = new Card(cardId, UUID.randomUUID(), 0, Status.BLOCKED, now, now);
        CardDto cardDto = new CardDto(cardId, UUID.randomUUID(), 0, Status.BLOCKED, now, now);

        when(cardService.blockCard(cardId)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        // when / then
        mockMvc.perform(put("/cards/{cardId}/block", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void blockCard_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/cards/{cardId}/block", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}
