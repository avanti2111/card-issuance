package com.example.cardissuance.controller;

import com.example.cardissuance.dto.CardRequest;
import com.example.cardissuance.dto.SpendRequest;
import com.example.cardissuance.dto.SpendResponse;
import com.example.cardissuance.dto.TopUpRequest;
import com.example.cardissuance.dto.TransactionResponse;
import com.example.cardissuance.entity.Card;
import com.example.cardissuance.service.CardService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody CardRequest request) {
        Card card = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @PostMapping("/{id}/spend")
    public ResponseEntity<SpendResponse> spend(
            @PathVariable Long id,
            @RequestBody SpendRequest request) {

        SpendResponse response = cardService.spend(id, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCard(id));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getTransactions(id));
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<SpendResponse> topUp(
        @PathVariable Long id,
        @RequestBody TopUpRequest request) {

    return ResponseEntity.ok(
            cardService.topUp(id, request.getAmount())
    );
    }

}

