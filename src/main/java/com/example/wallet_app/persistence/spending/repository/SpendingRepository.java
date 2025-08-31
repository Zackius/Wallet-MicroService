package com.example.wallet_app.persistence.spending.repository;

import com.example.wallet_app.persistence.spending.entities.Spending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface SpendingRepository extends JpaRepository<Spending, Long> {

    Optional<Spending> findByIdompotencyKey(String key);

    //Calculate Single customer spending amount

    @Query("SELECT COALESCE(SUM(s.amount) + SUM(s.totalFee), 0) " +
            "FROM Spending s " +
            "WHERE s.status IN ('SUCCESS', 'PENDING') " +
            "AND s.customerId.uuid = :customerUuid")
    BigDecimal getTotalCustomerSpending(UUID customerUuid);

}
