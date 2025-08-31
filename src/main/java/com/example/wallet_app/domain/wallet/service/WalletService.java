package com.example.wallet_app.domain.wallet.service;

import com.example.wallet_app.domain.ledger.dto.NewCollectionLedgerDto;
import com.example.wallet_app.domain.ledger.dto.NewSpendingLedgerDto;
import com.example.wallet_app.domain.ledger.queue.producer.CollectionLedgerProducer;
import com.example.wallet_app.domain.ledger.queue.producer.SpendingLedgerProducer;
import com.example.wallet_app.domain.wallet.dto.BalanceDto;
import com.example.wallet_app.domain.wallet.dto.CollectionDto;
import com.example.wallet_app.domain.wallet.dto.SingleCollectionDto;
import com.example.wallet_app.domain.wallet.dto.SpendingDto;
import com.example.wallet_app.dto.CustomResponse;
import com.example.wallet_app.exceptions.BadRequestException;
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
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.example.wallet_app.utils.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {
    private final CustomerRepository customerRepository;
    private final CollectionRepository collectionRepository;
    private final SpendingRepository spendingRepository;
    private final WalletAggregatorService  walletAggregatorService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final CollectionLedgerProducer collectionLedgerProducer;
    private final SpendingLedgerProducer spendingLedgerProducer;


    private final ObjectMapper objectMapper;
    public CustomResponse<SingleCollectionDto> createCollection(CollectionDto collectionDto, String customerId){
        //check if the client is present

        Optional<Customer> customer = customerRepository.findByUuid(UUID.fromString(customerId));

        if(customer.isEmpty()){
            throw new NotFoundException(CUSTOMER_NOT_FOUND);
        }

        if(collectionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException(INVALID_AMOUNT);
        }

        //check if the client has a collection record

        Optional<Collection> presentCollection = collectionRepository.findByIdompotencyKey(collectionDto.getKey());


        //check if transaction id is present
        Optional<Collection> transactionPresent = collectionRepository.findByReferenceNumber(collectionDto.getReferenceNumber());


                if(transactionPresent.isPresent()){
                    throw  new ConflictException("Transaction Id already exists");
                }
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
            collection.setReferenceNumber(collectionDto.getReferenceNumber());
            collection.setIdompotencyKey(collectionDto.getKey());


            Collection newCollection = collectionRepository.save(collection);

            //return the object using a dto to mask db values

            SingleCollectionDto toReturnCollection = new SingleCollectionDto();
            toReturnCollection.setRequestId(String.valueOf(newCollection.getUuid()));
            toReturnCollection.setReference(newCollection.getReferenceNumber());
            toReturnCollection.setAmount(newCollection.getAmount());


        NewCollectionLedgerDto  newCollectionLedgerDto = new NewCollectionLedgerDto();

        newCollectionLedgerDto.setCustomerId(String.valueOf(customer.get().getUuid()));
        newCollectionLedgerDto.setAmount(newCollection.getAmount());
        newCollectionLedgerDto.setIncomeTransactionId(String.valueOf(newCollection.getUuid()));
        newCollectionLedgerDto.setReferenceNumber(newCollection.getReferenceNumber());
try{
    String jsonString = objectMapper.writeValueAsString(newCollectionLedgerDto);

    collectionLedgerProducer.sendMessage(jsonString);
}catch (Exception e){
    log.error("Exception in converting to string collection ledger",e);
}


        return new CustomResponse<>(
                    201, CREATED_SUCCESSFULLY, toReturnCollection);




    }

    public CustomResponse<SingleCollectionDto> createCustomerExpense(SpendingDto spendingDto,String customerId){
        //check if the client is present

        Optional<Customer> customer = customerRepository.findByUuid(UUID.fromString(customerId));

        if(customer.isEmpty()){
            throw new NotFoundException(CUSTOMER_NOT_FOUND);
        }


        if(spendingDto.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException(INVALID_AMOUNT);
        }
        Optional<Spending> spendingKey = spendingRepository.findByIdompotencyKey(spendingDto.getKey());
        if(spendingKey.isPresent()){
            throw new ConflictException("Transaction already processed");
        }


        // check if transaction id is present
        Optional<Spending> transactionPresent = spendingRepository.findByReferenceNumber(spendingDto.getReferenceNumber());
        if(transactionPresent.isPresent()){
            throw new ConflictException("Transaction Id already exists");
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
        newSpendingItem.setReferenceNumber(spendingDto.getReferenceNumber());

        Spending newSpending = spendingRepository.save(newSpendingItem);

        SingleCollectionDto toReturnCollection = new SingleCollectionDto();
        toReturnCollection.setRequestId(String.valueOf(newSpending.getUuid()));
        toReturnCollection.setReference(newSpending.getReferenceNumber());
        toReturnCollection.setAmount(newSpending.getAmount());

log.info("Already created new spending: {}", toReturnCollection);
        NewSpendingLedgerDto newSpendingLedgerDto = new NewSpendingLedgerDto();

        newSpendingLedgerDto.setCustomerId(String.valueOf(customer.get().getUuid()));
        newSpendingLedgerDto.setAmount(newSpending.getAmount());
        newSpendingLedgerDto.setReferenceNumber(newSpending.getReferenceNumber());
        newSpendingLedgerDto.setExpenseTransactionId(String.valueOf(newSpending.getUuid()));


        try{
            String jsonString = objectMapper.writeValueAsString(newSpendingLedgerDto);
            log.info("JsonString: {}", jsonString);
            spendingLedgerProducer.sendMessage(jsonString);
        }catch (Exception e){
            log.error("Exception in converting to string collection ledger",e);
        }


        return new CustomResponse<>(
                201, CREATED_SUCCESSFULLY, toReturnCollection);
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
