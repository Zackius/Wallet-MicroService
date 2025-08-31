package com.example.wallet_app.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQQueueConfig {

    @Value("${rabbitmq.queues.collection-ledger-request}")
    private String collectionLedgerRequestQueue;

    @Value("${rabbitmq.queues.spending-ledger-request}")
    private String spendingLedgerRequestQueue;

    @Bean
    public Queue collectionLedgerRequestQueue() {
        return QueueBuilder.durable(collectionLedgerRequestQueue).build();
    }

    @Bean
    public Queue spendingLedgerRequestQueue() {
        return QueueBuilder.durable(spendingLedgerRequestQueue).build();
    }
}