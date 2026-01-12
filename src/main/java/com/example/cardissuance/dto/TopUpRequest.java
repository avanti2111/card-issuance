package com.example.cardissuance.dto;

import java.math.BigDecimal;

public class TopUpRequest {
    private BigDecimal amount;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
