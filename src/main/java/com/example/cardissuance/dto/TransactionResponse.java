package com.example.cardissuance.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {

    private BigDecimal amount;
    private String type;
    private Instant createdAt;

    public TransactionResponse(BigDecimal amount, String type, Instant createdAt) {
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public BigDecimal getAmount() { return amount; }
    public String getType() { return type; }
    public Instant getCreatedAt() { return createdAt; }
}
