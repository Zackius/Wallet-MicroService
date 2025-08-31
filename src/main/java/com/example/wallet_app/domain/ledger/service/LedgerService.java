package com.example.wallet_app.domain.ledger.service;

import com.example.wallet_app.domain.ledger.dto.NewCollectionLedgerDto;
import com.example.wallet_app.domain.ledger.dto.NewSpendingLedgerDto;
import com.example.wallet_app.dto.CustomResponse;
import com.example.wallet_app.persistence.collection.entities.Collection;
import com.example.wallet_app.persistence.collection.repository.CollectionRepository;
import com.example.wallet_app.persistence.customer.entities.Customer;
import com.example.wallet_app.persistence.customer.repository.CustomerRepository;
import com.example.wallet_app.persistence.ledger.entities.Ledger;
import com.example.wallet_app.persistence.ledger.repository.LedgerRepository;
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

import static com.example.wallet_app.utils.Constants.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerRepository ledgerRepository;
    private final CustomerRepository customerRepository;
private final CollectionRepository collectionRepository;
private final SpendingRepository spendingRepository;
    public void createSingleCollectionLedger(NewCollectionLedgerDto newCollectionLedgerDto) {

        // check if there exist another ledger item that has the details
        //ie amount, type and collection

        // grab customer
       Customer customer = customerRepository.findByUuid(UUID.fromString(newCollectionLedgerDto.getCustomerId())).orElse(null);




        //grab collection record
        Collection collectionPresent =collectionRepository.findByUuid(UUID.fromString(newCollectionLedgerDto.getIncomeTransactionId())).orElse(null);



        Optional<Ledger> ledgerItemPresent =ledgerRepository.findByAmountAndTypeAndIncomeTransactionId(newCollectionLedgerDto.getAmount(),CREDIT,collectionPresent);


        if(ledgerItemPresent.isPresent()){
            log.info("Ledger is already present: {}",ledgerItemPresent.get().getId());
        }else{
            // go ahead and create the ledger
            // check if there exist another ledger present for the customer
            Optional<Ledger> latestLedger = ledgerRepository.findTopByCustomerIdUuidOrderByCreatedAtDesc(customer.getUuid());
            log.info("Ledger is found: {}",latestLedger.get().getId());
            BigDecimal balance = BigDecimal.ZERO;
            balance = latestLedger.get().getBalance();


            LocalDateTime now = LocalDateTime.now();
            BigDecimal newBalance = balance.add(newCollectionLedgerDto.getAmount());
            // now go ahead and create the record
            Ledger ledger = new Ledger();
            ledger.setAmount(newCollectionLedgerDto.getAmount());
            ledger.setIncomeTransactionId(collectionPresent);
            ledger.setCustomerId(customer);
            ledger.setBalance(newBalance);
            ledger.setReferenceNumber(newCollectionLedgerDto.getReferenceNumber());
            ledger.setType(CREDIT);
            ledger.setCreatedAt(now);
            ledger.setUpdatedAt(now);
            ledger.setUuid(UUID.randomUUID());
            ledger.setTsid(TSIDGenerator.generateTSID());


            ledgerRepository.save(ledger);


        }





    }



    public void createSingleSpendingLedger(NewSpendingLedgerDto newSpendingLedgerDto) {
        // grab customer
        Customer customer = customerRepository.findByUuid(UUID.fromString(newSpendingLedgerDto.getCustomerId())).orElse(null);



        // grab expense
        Spending spending = spendingRepository.findByUuid(UUID.fromString(newSpendingLedgerDto.getExpenseTransactionId())).orElse(null);
        // check if ledger is present
        Optional<Ledger> ledgerPresent = ledgerRepository.findByAmountAndTypeAndExpenseTransactionId(newSpendingLedgerDto.getAmount(),DEBIT,spending);
        if(ledgerPresent.isPresent()){
            log.info("Ledger is already present: {}",ledgerPresent.get().getId());
        }else{
            // here we'll create 2 records
            //One for actual amount and the other one for fees


            Optional<Ledger> latestLedger = ledgerRepository.findTopByCustomerIdUuidOrderByCreatedAtDesc(customer.getUuid());
            BigDecimal balance = BigDecimal.ZERO;
            if(latestLedger.isPresent()){
                balance= latestLedger.get().getBalance();
            }

            LocalDateTime now = LocalDateTime.now();
            BigDecimal newBalance = balance.subtract(newSpendingLedgerDto.getAmount());
            Ledger ledger = new Ledger();
            ledger.setAmount(newSpendingLedgerDto.getAmount());
            ledger.setExpenseTransactionId(spending);
            ledger.setCustomerId(customer);
            ledger.setBalance(newBalance);
            ledger.setReferenceNumber(newSpendingLedgerDto.getReferenceNumber());
            ledger.setType(DEBIT);
            ledger.setCreatedAt(now);
            ledger.setUpdatedAt(now);
            ledger.setUuid(UUID.randomUUID());
            ledger.setTsid(TSIDGenerator.generateTSID());

            ledgerRepository.save(ledger);

            // go ahead and check if the fee is zero

            if(spending.getTotalFee().compareTo(BigDecimal.ZERO) > 0){
                // here we now create an item for the fee
                BigDecimal requiredBalance = newBalance.subtract(spending.getTotalFee());

                Ledger feeLedger = new Ledger();
                feeLedger.setAmount(spending.getTotalFee());
                feeLedger.setExpenseTransactionId(spending);
                feeLedger.setCustomerId(customer);
                feeLedger.setBalance(requiredBalance);
                feeLedger.setReferenceNumber(newSpendingLedgerDto.getReferenceNumber());
                feeLedger.setType(DEBIT);
                feeLedger.setCreatedAt(now);
                feeLedger.setUpdatedAt(now);
                feeLedger.setUuid(UUID.randomUUID());
                feeLedger.setTsid(TSIDGenerator.generateTSID());

                ledgerRepository.save(feeLedger);

            }
        }
    }

}
