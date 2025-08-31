package com.example.wallet_app.domain.reconciliation.service;


import com.example.wallet_app.domain.reconciliation.dto.ExternalTransactionDTO;
import com.example.wallet_app.persistence.collection.entities.Collection;
import com.example.wallet_app.persistence.collection.repository.CollectionRepository;
import com.example.wallet_app.persistence.reconciliation.entities.Reconciliation;
import com.example.wallet_app.persistence.reconciliation.repository.ReconciliationRepository;
import com.example.wallet_app.persistence.spending.entities.Spending;
import com.example.wallet_app.persistence.spending.repository.SpendingRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconciliationService {

    private final ReconciliationRepository reconciliationRepository;
    private final ObjectMapper objectMapper;
    private final CollectionRepository collectionRepository;
    private final SpendingRepository spendingRepository;


    /// TODO implement reconciliation from a json or a csv file here
    // Implement file upload here
    // grab all the items there and read them and arrange them int a json file to save
    // log the payload passed and grabbed.

    //Create the controller also to enable file upload in spring boot
    public Map<String, Object> uploadReconciliationFile(MultipartFile file, LocalDate date) throws Exception {

        String content;


        log.info("Date file :{}", date);

        if (file.getOriginalFilename().endsWith(".csv")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            List<String> lines = reader.lines().collect(Collectors.toList());

            // simple CSV: transactionId,amount
            List<String> jsonItems = lines.stream().skip(1) // skip header
                    .map(line -> {
                        String[] cols = line.split(",");
                        return String.format("{\"transactionId\":\"%s\", \"amount\":%s}", cols[0], cols[1]);
                    })
                    .toList();

            content = "[" + String.join(",", jsonItems) + "]";
        } else {
            // JSON file: just read as string
            content = new String(file.getBytes());


        }

        // here log the content
        log.info("File uploaded successfully, :{}", content);


        // go and create the item here Json
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setDate(date);
        reconciliation.setUuid(UUID.randomUUID());
        reconciliation.setExternalPayloadJson(content);
        reconciliation.setCreatedAt(LocalDateTime.now());
        reconciliation.setUpdatedAt(LocalDateTime.now());

        reconciliationRepository.save(reconciliation);

        // process the json

        return processJson(content);

    }


    // here we process the items and return the mismatched items using the transaction Ids
    public Map<String, Object> processJson(String jsonItems) throws Exception {

        // 1. Parse external JSON → DTOs
        ExternalTransactionDTO[] externalTxs = objectMapper.readValue(
                jsonItems,
                ExternalTransactionDTO[].class
        );
        List<ExternalTransactionDTO> externalList = Arrays.asList(externalTxs);

        // Build external map
        Map<String, BigDecimal> externalMap = externalList.stream()
                .collect(Collectors.toMap(
                        ExternalTransactionDTO::getTransactionId,
                        ExternalTransactionDTO::getAmount
                ));

        Set<String> allRefs = externalMap.keySet();

        // 2. Fetch internal transactions from DB
        List<Collection> collections = collectionRepository.findByReferenceNumberIn(new ArrayList<>(allRefs));
        List<Spending> spendings = spendingRepository.findByReferenceNumberIn(new ArrayList<>(allRefs));


        List<Collection> collectionsAll = collectionRepository.findAll();
        List<Spending> spendingsAll = spendingRepository.findAll();


        Map<String, BigDecimal> internalMapAll = new HashMap<>();
        collectionsAll.forEach(c -> internalMapAll.put(c.getReferenceNumber(), c.getAmount()));
        spendingsAll.forEach(s -> internalMapAll.put(s.getReferenceNumber(), s.getAmount()));


        // Merge into one internal map (referenceNumber → amount)
        Map<String, BigDecimal> internalMap = new HashMap<>();
        collections.forEach(c -> internalMap.put(c.getReferenceNumber(), c.getAmount()));
        spendings.forEach(s -> internalMap.put(s.getReferenceNumber(), s.getAmount()));

        // 3. Compare
        List<Map<String, Object>> matched = new ArrayList<>();
        List<Map<String, Object>> mismatched = new ArrayList<>();
        List<String> missingInternal = new ArrayList<>();
        List<String> missingExternal = new ArrayList<>();

        // external vs internal
        for (String txId : externalMap.keySet()) {
            BigDecimal externalAmt = externalMap.get(txId);
            if (internalMap.containsKey(txId)) {
                BigDecimal internalAmt = internalMap.get(txId);
                if (internalAmt.compareTo(externalAmt) == 0) {
                    matched.add(Map.of("transactionId", txId, "amount", externalAmt));
                } else {
                    mismatched.add(Map.of(
                            "transactionId", txId,
                            "externalAmount", externalAmt,
                            "internalAmount", internalAmt
                    ));
                }
            } else {
                missingInternal.add(txId);
            }
        }

        // internal transactions missing in external
        for (String txId : internalMapAll.keySet()) {
            log.info("txId internally ++++++++++ :{}", txId);
            if (!externalMap.containsKey(txId)) {
                missingExternal.add(txId);
            }
        }

        // 4. Return summary
        return Map.of(
                "matched", matched,
                "mismatched", mismatched,
                "missingInInternal", missingInternal,
                "missingInExternal", missingExternal
        );
    }


    // TODO Here implement an api endpoint to handle the items from user point

    public Map<String, Object> getReconciliationReport(LocalDate date) throws Exception {
        // grab a reconciliation with this date

        List<Reconciliation> reconciliations = reconciliationRepository.findByDate(date);

        List<Object> allTransactions = new ArrayList<>(); // Collect all transactions

        for (Reconciliation reconciliation : reconciliations) {
            try {
                String jsonString = reconciliation.getExternalPayloadJson();
                JsonNode jsonNode = objectMapper.readTree(jsonString);

                if (jsonNode.isArray()) {
                    // Add all array elements directly
                    List<Object> arrayData = objectMapper.convertValue(jsonNode, new TypeReference<List<Object>>(){});
                    allTransactions.addAll(arrayData);
                } else if (jsonNode.isObject()) {
                    // If it's an object, check if it contains transaction arrays
                    Map<String, Object> objectData = objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>(){});

                    // Look for transaction-like arrays in the object
                    for (Object value : objectData.values()) {
                        if (value instanceof List) {
                            allTransactions.addAll((List<?>) value);
                        }
                    }

                    // Or if the object itself represents a single transaction, add it
                    // allTransactions.add(objectData);
                } else {
                    log.warn("Unexpected JSON structure for reconciliation {}: {}",
                            reconciliation.getId(), jsonString.substring(0, Math.min(100, jsonString.length())));
                }

            } catch (Exception e) {
                log.error("Error parsing externalPayloadJson for reconciliation {}: {}", reconciliation.getId(), e.getMessage());
                throw new RuntimeException("Error parsing externalPayloadJson", e);
            }
        }

        // Deduplicate transactions (assuming they have some unique identifier)
        List<Object> deduplicatedTransactions = allTransactions.stream()
                .distinct()
                .toList();

        // Convert back to JSON array for processJson
        String reconciliationJson = objectMapper.writeValueAsString(deduplicatedTransactions);


        return processJson(reconciliationJson);
    }
}



