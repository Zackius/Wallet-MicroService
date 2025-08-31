package com.example.wallet_app.persistence.reconciliation.repository;

import com.example.wallet_app.persistence.reconciliation.entities.Reconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReconciliationRepository  extends JpaRepository<Reconciliation,Long> {


    List<Reconciliation> findByDate(LocalDate date);

}
