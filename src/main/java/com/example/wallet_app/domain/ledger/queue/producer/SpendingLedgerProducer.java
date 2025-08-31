package com.example.wallet_app.domain.ledger.queue.producer;

import com.example.wallet_app.queue.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SpendingLedgerProducer extends MessageProducer {

    @Value("${rabbitmq.queues.spending-ledger-request}")
    private String spendingLedgerRequestQueueName;
    private final RabbitTemplate rabbitTemplate;


    @Override
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(spendingLedgerRequestQueueName, message);
    }
}
