package com.example.cardissuance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardholderName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    private Instant createdAt;

    @Version
    private Long version;

    public Long getId() {
        return id;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}