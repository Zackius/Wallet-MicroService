package com.example.wallet_app.persistence.reconciliation.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="reconciliation")
@Entity
@Getter
@Setter
public class Reconciliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(unique = true, nullable = false, name = "uuid")
    private UUID uuid;

@Column(name="date")
private LocalDate date;

    @Column(columnDefinition = "jsonb",name="json")
    @Type(JsonType.class)
    private String externalPayloadJson; // save raw uploaded file as JSON

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
