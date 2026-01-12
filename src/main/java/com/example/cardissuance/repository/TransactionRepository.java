package com.example.cardissuance.repository;

import com.example.cardissuance.entity.Transaction;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   List<Transaction> findByCardId(Long cardId);
}
