package com.example.wallet_app.persistence.ledger.entities;

import com.example.wallet_app.persistence.collection.entities.Collection;
import com.example.wallet_app.persistence.customer.entities.Customer;
import com.example.wallet_app.persistence.spending.entities.Spending;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="ledger")
@Entity
@Getter
@Setter
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(unique = true, nullable = false, name = "uuid")
    private UUID uuid;
    @Column(unique = true, nullable = false,  name="tsid" )
    private String tsid;
    @ManyToOne
    @JoinColumn(name="customerId")
    private Customer customerId;
    @Column(name="reference_number")
    private String referenceNumber;
    @Column(name="amount")
    private BigDecimal amount;
    @Column(name="balance")
    private BigDecimal balance;
    @ManyToOne
    @JoinColumn(name="income_transaction_id", nullable = true)
    private Collection incomeTransactionId;
    @ManyToOne
    @JoinColumn(name="expense_transaction_id", nullable = true)
    private Spending expenseTransactionId;
    @Column(name="type",  nullable = true)
    private String type;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
