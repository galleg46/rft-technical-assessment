package com.rft.orderProcessing.models;

import lombok.Getter;
import lombok.Setter;

public class OrderResponse {

    @Getter
    @Setter
    String orderId;

    @Getter
    @Setter
    String status;

    public OrderResponse() {}

    public OrderResponse(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }
}
