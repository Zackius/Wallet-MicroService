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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

//This handles spending
public class NewSpendingLedgerDto {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    @NotBlank(message = "Customer id required")
    private String customerId;
    @NotBlank(message="Reference Number is needed")
    private String referenceNumber;
    @NotBlank(message="incomeTransactionId Number is needed")

    private String expenseTransactionId;
    @NotBlank(message="type  is needed")
    private String type;
}
