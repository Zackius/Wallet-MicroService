package com.example.wallet_app.domain.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerDto {
    @NotBlank(message = "Name is required")
    private String name;


//    private String collectionCallbackUrl;
}
