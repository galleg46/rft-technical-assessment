package com.rft.orderProcessing.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Order {

    @Getter
    @Setter
    String customerId;

    @Getter
    @Setter
    List<Item> items;

    public Order() {}

    public Order(String customerId, List<Item> items) {
        this.customerId = customerId;
        this.items = items;
    }
}
