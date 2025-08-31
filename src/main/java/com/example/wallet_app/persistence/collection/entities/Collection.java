package com.example.wallet_app.persistence.collection.entities;


import com.example.wallet_app.persistence.customer.entities.Customer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="collection")
@Entity
@Getter
@Setter

public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(unique = true, nullable = false, name = "uuid")
    private UUID uuid;
    @Column(unique = true, nullable = false,  name="tsid" )
    private String tsid;
    @Column(name="reference_number")
    private String referenceNumber;
    @ManyToOne
    @JoinColumn(name="customerId")
    private Customer customerId;
    @Column(name="amount")
    private BigDecimal amount;
    @Column(name="fee")
    private BigDecimal fee=BigDecimal.ZERO;
    @Column(name="idompotency_key")
    private String idompotencyKey;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;





}
