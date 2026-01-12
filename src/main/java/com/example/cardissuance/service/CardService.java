package com.example.cardissuance.service;


import com.example.cardissuance.dto.CardRequest;
import com.example.cardissuance.dto.SpendResponse;
import com.example.cardissuance.dto.TransactionResponse;
import com.example.cardissuance.entity.Card;
import com.example.cardissuance.entity.Transaction;
import com.example.cardissuance.exception.CardNotFoundException;
import com.example.cardissuance.exception.InsufficientBalanceException;
import com.example.cardissuance.repository.CardRepository;
import com.example.cardissuance.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    public CardService(CardRepository cardRepository,
                   TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }

    public Card createCard(CardRequest request) {
        if (request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        Card card = new Card();
        card.setCardholderName(request.getCardholderName());
        card.setBalance(request.getInitialBalance());
        card.setCreatedAt(Instant.now());

        return cardRepository.saveAndFlush(card);
    }

    @Transactional
    public SpendResponse spend(Long cardId, BigDecimal amount) {
    Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException(cardId));

    if (amount.compareTo(card.getBalance()) > 0) {
        throw new InsufficientBalanceException();
    }

    card.setBalance(card.getBalance().subtract(amount));
    cardRepository.save(card);

    transactionRepository.save(
            new Transaction(card, amount, "SPEND")
    );

    return new SpendResponse(card.getId(), card.getBalance());
    }

    public Card getCard(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long cardId) {
    cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException(cardId));

    return transactionRepository.findByCardId(cardId)
            .stream()
            .map(tx -> new TransactionResponse(
                    tx.getAmount(),
                    tx.getType(),
                    tx.getCreatedAt()
            ))
            .toList();
    }

    @Transactional
    public SpendResponse topUp(Long cardId, BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Top-up amount must be positive");
    }

    Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException(cardId));

    card.setBalance(card.getBalance().add(amount));
    cardRepository.save(card);

    transactionRepository.save(
        new Transaction(card, amount, "TOPUP")
    );
       return new SpendResponse(card.getId(), card.getBalance());
    }

}
