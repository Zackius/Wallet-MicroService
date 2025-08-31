package com.example.wallet_app.domain.wallet.service;

import com.example.wallet_app.persistence.collection.repository.CollectionRepository;
import com.example.wallet_app.persistence.spending.repository.SpendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@RequiredArgsConstructor
public class WalletAggregatorService {

    private final CollectionRepository collectionRepository;
    private final SpendingRepository spendingRepository;

    //TODO implement Completable future to calculate wallets
private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    public CompletableFuture<BigDecimal>getTotalWallet(UUID customerUuid){



        CompletableFuture<BigDecimal> spendingFuture = CompletableFuture.supplyAsync(()-> spendingRepository.getTotalCustomerSpending(customerUuid),executorService);


        CompletableFuture<BigDecimal> collectionFuture = CompletableFuture.supplyAsync(()-> collectionRepository.getTotalCustomerCollection(customerUuid),executorService);

        // Combine both futures and subtract
        return collectionFuture.thenCombine(spendingFuture,
                (collection, spending) -> {
                    if (collection == null) collection = BigDecimal.ZERO;
                    if (spending == null) spending = BigDecimal.ZERO;
                    return collection.subtract(spending);
                });
    }



}
