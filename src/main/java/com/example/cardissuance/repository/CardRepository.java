package com.example.cardissuance.repository;

import com.example.cardissuance.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    
}