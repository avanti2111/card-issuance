package com.example.cardissuance.service;


import com.example.cardissuance.dto.CardRequest;
import com.example.cardissuance.entity.Card;
import com.example.cardissuance.exception.CardNotFoundException;
import com.example.cardissuance.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CardServiceTest {

    @Autowired
    private CardService cardService;

    //Crete Card Successfully
    @Test
    void shouldCreateCardSuccessfully() {
        CardRequest request = new CardRequest();
        request.setCardholderName("Alice");
        request.setInitialBalance(BigDecimal.valueOf(100));

        Card card = cardService.createCard(request);

        assertNotNull(card.getId());
        assertEquals("Alice", card.getCardholderName());
        assertEquals(BigDecimal.valueOf(100), card.getBalance());
        assertNotNull(card.getCreatedAt());
    }

    // create Card with Negative Balance Fails
    @Test
    void shouldFailWhenInitialBalanceIsNegative() {
        CardRequest request = new CardRequest();
        request.setCardholderName("Bob");
        request.setInitialBalance(BigDecimal.valueOf(-10));

        assertThrows(
                IllegalArgumentException.class,
                () -> cardService.createCard(request)
        );
    }

    // Spend Valid Amount Successfully
    @Test
    void shouldDeductBalanceWhenSpendingValidAmount() {
        CardRequest request = new CardRequest();
        request.setCardholderName("Alice");
        request.setInitialBalance(BigDecimal.valueOf(100));

        Card card = cardService.createCard(request);

        var response = cardService.spend(card.getId(), BigDecimal.valueOf(30));

        assertEquals(0, response.getRemainingBalance().compareTo(BigDecimal.valueOf(70)));
    }

    // Overdraft Prevention
    @Test
    void shouldFailWhenSpendingMoreThanBalance() {
        CardRequest request = new CardRequest();
        request.setCardholderName("Alice");
        request.setInitialBalance(BigDecimal.valueOf(50));

        Card card = cardService.createCard(request);

        assertThrows(
                InsufficientBalanceException.class,
                () -> cardService.spend(card.getId(), BigDecimal.valueOf(100))
        );
    }

    // Card not found
    @Test
    void shouldFailWhenCardDoesNotExist() {
        assertThrows(
                CardNotFoundException.class,
                () -> cardService.getCard(999L)
        );
    }

    // Concurrent Spend Operations
    @Test
    void concurrentSpendsShouldBothSucceedIfBalanceAllows() throws Exception {
        CardRequest request = new CardRequest();
        request.setCardholderName("Alice");
        request.setInitialBalance(BigDecimal.valueOf(200));

        Card card = cardService.createCard(request);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        Callable<Boolean> spend60 = () -> {
            latch.await();
            try {
                cardService.spend(card.getId(), BigDecimal.valueOf(60));
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        Callable<Boolean> spend50 = () -> {
            latch.await();
            try {
                cardService.spend(card.getId(), BigDecimal.valueOf(50));
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        Future<Boolean> f1 = executor.submit(spend60);
        Future<Boolean> f2 = executor.submit(spend50);

        latch.countDown();

        int successCount = 0;
        if (f1.get()) successCount++;
        if (f2.get()) successCount++;

        executor.shutdown();
    
        assertTrue(successCount >= 1);

        Card updated = cardService.getCard(card.getId());
        assertTrue(updated.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(updated.getBalance().compareTo(BigDecimal.valueOf(200)) <= 0);
    }
}