package com.example.wallet_app.persistence.ledger.repository;

import com.example.wallet_app.persistence.collection.entities.Collection;
import com.example.wallet_app.persistence.ledger.entities.Ledger;
import com.example.wallet_app.persistence.spending.entities.Spending;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface LedgerRepository extends CrudRepository<Ledger, Long> {


    //This one is for collection

    Optional<Ledger> findByAmountAndTypeAndIncomeTransactionId(BigDecimal amount, String type, Collection incomeTransactionId);

    // This one is for spending
    Optional<Ledger>findByAmountAndTypeAndExpenseTransactionId(BigDecimal amount, String type, Spending expenseTransactionId);

    Optional<Ledger>findTopByCustomerIdUuidOrderByCreatedAtDesc(UUID customerId);
}
