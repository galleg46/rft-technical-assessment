package com.rft.orderProcessing.models;

import lombok.Getter;
import lombok.Setter;

public class OrderUpdateRequest {

    @Getter
    @Setter
    String status;

    public OrderUpdateRequest() {}

    public OrderUpdateRequest(String status) {
        this.status = status;
    }
}
