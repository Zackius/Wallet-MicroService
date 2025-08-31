package com.example.wallet_app.domain.ledger.dto;

import com.example.wallet_app.persistence.collection.entities.Collection;
import com.example.wallet_app.persistence.customer.entities.Customer;
import com.example.wallet_app.persistence.spending.entities.Spending;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewCollectionLedgerDto {

    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    @NotBlank(message = "Customer id required")
    private String customerId;
    @NotBlank(message="Reference Number is needed")
    private String referenceNumber;
    @NotBlank(message="incomeTransactionId Number is needed")
    private String incomeTransactionId;
}
