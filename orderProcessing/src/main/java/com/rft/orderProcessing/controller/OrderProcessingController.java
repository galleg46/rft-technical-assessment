package com.rft.orderProcessing.controller;

import com.rft.orderProcessing.exceptions.InvalidOrderStateException;
import com.rft.orderProcessing.exceptions.OrderCreationException;
import com.rft.orderProcessing.exceptions.OrderNotFoundException;
import com.rft.orderProcessing.models.Order;
import com.rft.orderProcessing.models.OrderResponse;
import com.rft.orderProcessing.models.OrderUpdateRequest;
import com.rft.orderProcessing.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/orders")
public class OrderProcessingController {

    private final OrderService orderService;

    public OrderProcessingController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order request) {
        try {
            OrderResponse response = orderService.createOrder(request);
            return ResponseEntity.accepted().body(response);
        } catch (OrderCreationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

    }

    @GetMapping(path = "/{orderId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable Long orderId) {
        try {
            OrderResponse response = orderService.getOrder(orderId);
            return ResponseEntity.ok(response);
        } catch (OrderNotFoundException ex) {
            return ResponseEntity.ok(ex.getMessage());
        }
    }

    @PatchMapping(path = "/{orderId}")
    public ResponseEntity<?> updateOrderState(@PathVariable Long orderId, @RequestBody OrderUpdateRequest request) {
        try {
            OrderResponse response = orderService.updateOrderState(orderId, request.getStatus());
            return ResponseEntity.ok(response);
        } catch (InvalidOrderStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
