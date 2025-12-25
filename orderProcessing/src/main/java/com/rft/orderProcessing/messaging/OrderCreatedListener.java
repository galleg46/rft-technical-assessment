package com.rft.orderProcessing.messaging;

import com.rft.orderProcessing.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("Received order: " +event.getOrderId());
    }
}
