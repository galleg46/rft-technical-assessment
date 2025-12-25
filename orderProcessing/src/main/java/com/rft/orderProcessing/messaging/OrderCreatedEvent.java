package com.rft.orderProcessing.messaging;

public class OrderCreatedEvent {

    private String orderId;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
