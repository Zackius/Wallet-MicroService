package com.example.wallet_app.persistence.collection.repository;

import com.example.wallet_app.persistence.collection.entities.Collection;
import com.example.wallet_app.persistence.customer.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {


    Optional<Collection> findByIdompotencyKey(String key);
    Optional<Collection> findByUuid(UUID uuid);
    Optional<Collection> findByReferenceNumber(String referenceNumber);
    @Query("SELECT c.referenceNumber, c.amount FROM Collection c WHERE c.referenceNumber IN :refs")
    List<Object[]> findByReferenceNumbers(@Param("refs") List<String> refs);
    @Query("SELECT COALESCE(SUM(c.amount), 0) + COALESCE(SUM(c.fee), 0) " +
            "FROM Collection c " +
            "WHERE c.customerId.uuid = :customerUuid")
    BigDecimal getTotalCustomerCollection(UUID customerUuid);

    List<Collection> findByReferenceNumberIn(List<String> refs);
}
