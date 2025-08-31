package com.example.wallet_app.domain.reconciliation.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalTransactionDTO {
    private String transactionId;
    private BigDecimal amount;
}
