package com.example.cardissuance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // POST /cards
    @Test
    void shouldCreateCard() throws Exception {
        mockMvc.perform(post("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "cardholderName": "Alice",
                          "initialBalance": 100
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.balance").value(100));
    }

    // POST /cards/{id}/spend
    @Test
    void shouldSpendSuccessfully() throws Exception {
        Long cardId = createCardAndReturnId(100);

        mockMvc.perform(post("/cards/{id}/spend", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": 40
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingBalance").value(60));
    }

    // OVERDRAFT
    @Test
    void shouldRejectOverdraftViaApi() throws Exception {
        Long cardId = createCardAndReturnId(30);

        mockMvc.perform(post("/cards/{id}/spend", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": 100
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    // GET /cards/{id}
    @Test
    void shouldGetCardDetails() throws Exception {
        Long cardId = createCardAndReturnId(80);

        mockMvc.perform(get("/cards/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(80))
                .andExpect(jsonPath("$.cardholderName").value("Alice"));
    }

    //GET non-existing card
    @Test
    void shouldReturn404WhenCardNotFound() throws Exception {
        mockMvc.perform(get("/cards/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    // Helper method to create a card and return its ID for each test case
    private Long createCardAndReturnId(int balance) throws Exception {
        MvcResult result = mockMvc.perform(post("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "cardholderName": "Alice",
                          "initialBalance": %d
                        }
                        """.formatted(balance)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    // POST /cards/{id}/topup
    @Test
    void shouldTopUpSuccessfully() throws Exception {
        Long cardId = createCardAndReturnId(50);

        mockMvc.perform(post("/cards/{id}/topup", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": 30
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingBalance").value(80));
    }

    // Top-up with invalid amount
    @Test
    void shouldFailTopUpWithNegativeAmount() throws Exception {
        Long cardId = createCardAndReturnId(50);

        mockMvc.perform(post("/cards/{id}/topup", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": -10
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    // GET /cards/{id}/transactions
    @Test
    void shouldReturnTransactionHistory() throws Exception {
        Long cardId = createCardAndReturnId(100);

        // spend
        mockMvc.perform(post("/cards/{id}/spend", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": 40
                        }
                        """))
                .andExpect(status().isOk());

        // top-up
        mockMvc.perform(post("/cards/{id}/topup", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "amount": 20
                        }
                        """))
                .andExpect(status().isOk());

        // fetch transactions
        mockMvc.perform(get("/cards/{id}/transactions", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").exists())
                .andExpect(jsonPath("$[0].amount").exists());
    }
}
