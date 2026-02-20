package com.codillas.loyaltycard.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codillas.loyaltycard.controller.dto.CustomerDto;
import com.codillas.loyaltycard.mapper.CustomerMapper;
import com.codillas.loyaltycard.repository.AdminRepository;
import com.codillas.loyaltycard.repository.CardRepository;
import com.codillas.loyaltycard.repository.CustomerRepository;
import com.codillas.loyaltycard.repository.TransactionRepository;
import com.codillas.loyaltycard.service.CustomerService;
import com.codillas.loyaltycard.service.model.Customer;
import com.codillas.loyaltycard.service.model.Status;
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
class CustomerControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private CustomerMapper customerMapper;

    @MockitoBean
    private CustomerRepository customerRepository;
    @MockitoBean
    private CardRepository cardRepository;
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

    // ─── GET /customers ──────────────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getCustomers_shouldReturn200WithListOfCustomers() throws Exception {
        Customer customer = buildCustomer("alice@test.com");
        CustomerDto customerDto = buildCustomerDto("alice@test.com");

        when(customerService.getCustomers()).thenReturn(List.of(customer));
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("alice@test.com"));
    }

    @Test
    void getCustomers_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(customerService);
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void getCustomers_shouldReturn403_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isForbidden());
    }

    // ─── GET /customers/{customerId} ─────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void getCustomer_shouldReturn200WithCustomerDto() throws Exception {
        UUID customerId = UUID.randomUUID();
        Customer customer = buildCustomer("bob@test.com");
        CustomerDto customerDto = buildCustomerDto("bob@test.com");

        when(customerService.getCustomer(customerId)).thenReturn(customer);
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        mockMvc.perform(get("/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@test.com"));
    }

    @Test
    void getCustomer_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/customers/{customerId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    // ─── PUT /customers/{customerId} ─────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void updateCustomer_shouldReturn200WithUpdatedCustomerDto() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerDto requestDto = buildCustomerDto("updated@test.com");
        Customer updatedCustomer = buildCustomer("updated@test.com");
        CustomerDto responseDto = buildCustomerDto("updated@test.com");

        when(customerService.updateCustomer(customerId, requestDto.getName(),
                requestDto.getPhoneNumber(), requestDto.getEmail()))
                .thenReturn(updatedCustomer);
        when(customerMapper.toDto(updatedCustomer)).thenReturn(responseDto);

        mockMvc.perform(put("/customers/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    void updateCustomer_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/customers/{customerId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildCustomerDto("test@test.com"))))
                .andExpect(status().isUnauthorized());
    }

    // ─── PUT /customers/{customerId}/activate ────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void activateCustomer_shouldReturn200() throws Exception {
        UUID customerId = UUID.randomUUID();
        Customer customer = buildCustomer("activate@test.com");
        CustomerDto customerDto = buildCustomerDto("activate@test.com");

        when(customerService.activateCustomer(customerId)).thenReturn(customer);
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        mockMvc.perform(put("/customers/{customerId}/activate", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void activateCustomer_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/customers/{customerId}/activate", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    // ─── PUT /customers/{customerId}/block ───────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    void blockCustomer_shouldReturn200() throws Exception {
        UUID customerId = UUID.randomUUID();
        Customer customer = buildCustomer("block@test.com");
        CustomerDto customerDto = buildCustomerDto("block@test.com");

        when(customerService.blockCustomer(customerId)).thenReturn(customer);
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        mockMvc.perform(put("/customers/{customerId}/block", customerId))
                .andExpect(status().isOk());
    }

    @Test
    void blockCustomer_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/customers/{customerId}/block", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Customer buildCustomer(String email) {
        return new Customer(UUID.randomUUID(), "Alice", "123456789", email, "hashed",
                Status.ACTIVE, null, Instant.now(), Instant.now());
    }

    private CustomerDto buildCustomerDto(String email) {
        return new CustomerDto(UUID.randomUUID(), "Alice", "123456789", email,
                Status.ACTIVE, null, null, null);
    }
}
