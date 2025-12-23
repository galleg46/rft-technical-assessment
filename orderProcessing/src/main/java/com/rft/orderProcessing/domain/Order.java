package com.rft.orderProcessing.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String customerId;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OrderState status;

    @ElementCollection
    private List<OrderItem> items;

    protected Order() {}

    public Order(String customerId, List<OrderItem> items) {
        this.customerId = customerId;
        this.items = items;
        this.status = OrderState.PENDING;
    }

}
