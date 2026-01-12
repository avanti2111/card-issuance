package com.example.cardissuance.dto;

import java.math.BigDecimal;

public class CardRequest {

    private String cardholderName;
    private BigDecimal initialBalance;

    public String getCardholderName() {
        return cardholderName;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
    
}
