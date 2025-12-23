package com.rft.orderProcessing.models;

import lombok.Getter;
import lombok.Setter;

public class Item {

    @Getter
    @Setter
    String sku;

    @Getter
    @Setter
    String quantity;

    public Item() {}

    public Item(String sku, String quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }
}
