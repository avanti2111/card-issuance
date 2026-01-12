package com.example.cardissuance.dto;

import java.math.BigDecimal;

public class SpendResponse {
    private Long id;
    private BigDecimal remainingBalance;

    public SpendResponse(Long id, BigDecimal remainingBalance) {
        this.id = id;
        this.remainingBalance = remainingBalance;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }
    
}
