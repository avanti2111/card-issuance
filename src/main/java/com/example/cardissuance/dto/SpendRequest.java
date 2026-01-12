package com.example.cardissuance.dto;

import java.math.BigDecimal;

public class SpendRequest {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
}
