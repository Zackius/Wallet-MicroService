package com.example.wallet_app.domain.ledger.queue.producer;

import com.example.wallet_app.queue.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CollectionLedgerProducer extends MessageProducer {


    @Value("${rabbitmq.queues.collection-ledger-request}")
    private String collectionLedgerRequestQueueName;
    private final RabbitTemplate rabbitTemplate;


    @Override
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(collectionLedgerRequestQueueName, message);
    }
}
