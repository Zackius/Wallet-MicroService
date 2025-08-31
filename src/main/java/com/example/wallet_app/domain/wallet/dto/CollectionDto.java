package com.example.wallet_app.domain.wallet.dto;

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
public class CollectionDto {

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Key id is required")
    private String key;

}
