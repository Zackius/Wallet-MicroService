package com.example.wallet_app.domain.ledger.queue.consumer;


import com.example.wallet_app.domain.ledger.dto.NewCollectionLedgerDto;
import com.example.wallet_app.domain.ledger.service.LedgerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor

public class CollectionLedgerListener {
    private final LedgerService ledgerService;

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queues.collection-ledger-request}", containerFactory =  "collectionFactory")
    public void receiveMessage(String message, Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        try{
            log.info("Received message: {}",message);
            NewCollectionLedgerDto   collectionLedgerResponse = objectMapper.readValue(message, NewCollectionLedgerDto.class);
            ledgerService.createSingleCollectionLedger(collectionLedgerResponse);
            channel.basicAck(tag,true);

        }catch (Exception e){
            log.error("Error while receiving ledger message:{}",e.getMessage());
            channel.basicAck(tag,false);
        }

    }
}
