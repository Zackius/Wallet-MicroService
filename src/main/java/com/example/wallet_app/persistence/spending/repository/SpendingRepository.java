package com.example.wallet_app.persistence.spending.repository;

import com.example.wallet_app.persistence.spending.entities.Spending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendingRepository extends JpaRepository<Spending, Long> {

    Optional<Spending> findByIdompotencyKey(String key);

    Optional<Spending>findByUuid(UUID uuid);
    Optional<Spending> findByReferenceNumber(String referenceNumber);

    //Calculate Single customer spending amount

    @Query("SELECT COALESCE(SUM(s.amount) + SUM(s.totalFee), 0) " +
            "FROM Spending s " +
            "WHERE s.status IN ('SUCCESS', 'PENDING') " +
            "AND s.customerId.uuid = :customerUuid")
    BigDecimal getTotalCustomerSpending(UUID customerUuid);

    @Query("SELECT s.referenceNumber, s.amount FROM Spending s WHERE s.referenceNumber IN :refs")
    List<Object[]> findByReferenceNumbers(@Param("refs") List<String> refs);

    List<Spending> findByReferenceNumberIn(List<String> refs);
}
