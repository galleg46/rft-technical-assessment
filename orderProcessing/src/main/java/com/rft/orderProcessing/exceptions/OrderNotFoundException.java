package com.rft.orderProcessing.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("Order not found with orderId: " +orderId);
    }
}
