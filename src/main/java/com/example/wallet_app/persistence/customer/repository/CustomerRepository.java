package com.example.wallet_app.persistence.customer.repository;

import com.example.wallet_app.persistence.customer.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByName(String name);

    Optional<Customer> findByUuid(UUID Uuid);

}
