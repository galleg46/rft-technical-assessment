package com.rft.orderProcessing.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderItem {

    private String sku;
    private String qty;

    protected OrderItem() {}

    public OrderItem(String sku, String qty) {
        this.sku = sku;
        this.qty = qty;
    }
}
