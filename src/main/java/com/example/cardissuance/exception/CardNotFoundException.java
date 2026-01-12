package com.example.cardissuance.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(Long id) {
        super("Card not found with id: " + id);
    }
}