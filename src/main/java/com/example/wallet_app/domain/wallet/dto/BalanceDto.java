package com.example.wallet_app.domain.wallet.dto;

import com.example.wallet_app.persistence.customer.entities.Customer;
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
public class BalanceDto {
    private BigDecimal spentAmount;
    private BigDecimal totalTopUpAmount;
    private BigDecimal balance;
    private Customer customer;

}
