package com.example.wallet_app.controllers;


import com.example.wallet_app.domain.wallet.dto.BalanceDto;
import com.example.wallet_app.domain.wallet.dto.CollectionDto;
import com.example.wallet_app.domain.wallet.dto.SingleCollectionDto;
import com.example.wallet_app.domain.wallet.dto.SpendingDto;
import com.example.wallet_app.domain.wallet.service.WalletService;
import com.example.wallet_app.dto.CustomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/wallets")
@RequiredArgsConstructor
public class CollectionController {
    private final WalletService walletService;


    @PostMapping("/topup/{id}")
    public ResponseEntity<CustomResponse<SingleCollectionDto>> newCollection(@RequestBody @Valid CollectionDto collectionDto, @PathVariable String id){
        CustomResponse<SingleCollectionDto> response = walletService.createCollection(collectionDto, id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/{id}/consume")
    public ResponseEntity<CustomResponse<SingleCollectionDto>> createCustomerExpense(@RequestBody  @Valid SpendingDto spendingDto, @PathVariable String id){
        CustomResponse<SingleCollectionDto> response = walletService.createCustomerExpense(spendingDto, id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @GetMapping("/{id}/balance")
    public ResponseEntity<CustomResponse<BalanceDto>> getCustomerBalance(@PathVariable String id){
        CustomResponse<BalanceDto> response = walletService.getCustomerBalance(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}

