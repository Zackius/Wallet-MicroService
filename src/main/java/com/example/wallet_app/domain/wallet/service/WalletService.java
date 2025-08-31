package com.example.wallet_app.domain.wallet.service;


import com.example.wallet_app.domain.customer.dto.SingleCustomerDto;
import com.example.wallet_app.domain.wallet.dto.BalanceDto;
import com.example.wallet_app.domain.wallet.dto.CollectionDto;
import com.example.wallet_app.domain.wallet.dto.SingleCollectionDto;
import com.example.wallet_app.domain.wallet.dto.SpendingDto;
import com.example.wallet_app.dto.CustomResponse;
import com.example.wallet_app.exceptions.ConflictException;
import com.example.wallet_app.exceptions.InsufficientWalletException;
import com.example.wallet_app.exceptions.NotFoundException;
import com.example.wallet_app.persistence.collection.repository.CollectionRepository;
import com.example.wallet_app.persistence.customer.entities.Customer;
import com.example.wallet_app.persistence.collection.entities.Collection;
import com.example.wallet_app.persistence.customer.repository.CustomerRepository;
import com.example.wallet_app.persistence.spending.entities.Spending;
import com.example.wallet_app.persistence.spending.repository.SpendingRepository;
import com.example.wallet_app.utils.TSIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.wallet_app.utils.Constants.SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {
    private final CustomerRepository customerRepository;
    private final CollectionRepository collectionRepository;
    private final SpendingRepository spendingRepository;
    private final WalletAggregatorService  walletAggregatorService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public CustomResponse<SingleCollectionDto> createCollection(CollectionDto collectionDto, String customerId){
        //check if the client is present

        Optional<Customer> customer = customerRepository.findByUuid(UUID.fromString(customerId));

        if(customer.isEmpty()){
            throw new NotFoundException("Customer not exists");
        }

        //check if the client has a collection record

        Optional<Collection> presentCollection = collectionRepository.findByIdompotencyKey(collectionDto.getKey());


        if(presentCollection.isPresent()){
            throw new ConflictException("Collection already exists");
        }

            LocalDateTime now = LocalDateTime.now();

            Collection collection = new Collection();
            collection.setCreatedAt(now);
            collection.setUpdatedAt(now);
            collection.setTsid(TSIDGenerator.generateTSID());
            collection.setUuid(UUID.randomUUID());
            collection.setAmount(collectionDto.getAmount());
            collection.setCustomerId(customer.get());
            collection.setReferenceNumber(UUID.randomUUID().toString());
            collection.setIdompotencyKey(collectionDto.getKey());


            Collection newCollection = collectionRepository.save(collection);

            //return the object using a dto to mask db values

            SingleCollectionDto toReturnCollection = new SingleCollectionDto();
            toReturnCollection.setRequestId(String.valueOf(newCollection.getUuid()));
            toReturnCollection.setReference(newCollection.getReferenceNumber());
            toReturnCollection.setAmount(newCollection.getAmount());

            return new CustomResponse<>(
                    201, SUCCESS, toReturnCollection);




    }

    public CustomResponse<SingleCollectionDto> createCustomerExpense(SpendingDto spendingDto,String customerId){
        //check if the client is present

        Optional<Customer> customer = customerRepository.findByUuid(UUID.fromString(customerId));

        if(customer.isEmpty()){
            throw new NotFoundException("Customer not exists");
        }

        Optional<Spending> spendingKey = spendingRepository.findByIdompotencyKey(spendingDto.getKey());
        if(spendingKey.isPresent()){
            throw new ConflictException("Transaction already processed");
        }



        // check if customer has enough money to spend

        CompletableFuture<BigDecimal> walletAmount = walletAggregatorService.getTotalWallet(customer.get().getUuid());

        BigDecimal walletJoin = walletAmount.join();
        log.info("wallet join: {}", walletJoin);

        if (spendingDto.getAmount().compareTo(walletJoin) > 0){

            // throw insufficient wallet balance
            throw new InsufficientWalletException("Insufficient wallet");


        }


        // go ahead and create the item
        Spending newSpendingItem = new Spending();
        newSpendingItem.setAmount(spendingDto.getAmount());
        newSpendingItem.setCustomerId(customer.get());
        newSpendingItem.setSpendMode(spendingDto.getMode().toUpperCase());
        newSpendingItem.setIdompotencyKey(spendingDto.getKey());
        newSpendingItem.setTsid(TSIDGenerator.generateTSID());
        newSpendingItem.setUuid(UUID.randomUUID());
        newSpendingItem.setCreatedAt(LocalDateTime.now());
        newSpendingItem.setUpdatedAt(LocalDateTime.now());
        newSpendingItem.setReferenceNumber(UUID.randomUUID().toString());

        Spending newSpending = spendingRepository.save(newSpendingItem);

        SingleCollectionDto toReturnCollection = new SingleCollectionDto();
        toReturnCollection.setRequestId(String.valueOf(newSpending.getUuid()));
        toReturnCollection.setReference(newSpending.getReferenceNumber());
        toReturnCollection.setAmount(newSpending.getAmount());

        return new CustomResponse<>(
                201, SUCCESS, toReturnCollection);
    }

    public CustomResponse<BalanceDto> getCustomerBalance(String customerId){

        //check if the client is present

        Optional<Customer> customer = customerRepository.findByUuid(UUID.fromString(customerId));

        if(customer.isEmpty()){
            throw new NotFoundException("Customer not exists");

        }

        // get total customer wallet balance
        CompletableFuture<BigDecimal> walletAmount = walletAggregatorService.getTotalWallet(customer.get().getUuid());


        //get customer  total spend

        CompletableFuture<BigDecimal> spendingFuture = CompletableFuture.supplyAsync(()-> spendingRepository.getTotalCustomerSpending(customer.get().getUuid()),executorService);

       // get customer total topUps
        CompletableFuture<BigDecimal> collectionFuture = CompletableFuture.supplyAsync(()-> collectionRepository.getTotalCustomerCollection(customer.get().getUuid()),executorService);


        CompletableFuture<BalanceDto> balanceDtoFuture =
                spendingFuture.thenCombine(collectionFuture, (spending, collection) -> {
                    if (spending == null) spending = BigDecimal.ZERO;
                    if (collection == null) collection = BigDecimal.ZERO;
                    return new BalanceDto(spending, collection, collection.subtract(spending), customer.get());
                });



        BalanceDto balanceDto = balanceDtoFuture.join();

        return new CustomResponse<>(200, SUCCESS,balanceDto);





    }
}
