package com.rft.orderProcessing.service;

import com.rft.orderProcessing.domain.OrderItem;
import com.rft.orderProcessing.domain.OrderState;
import com.rft.orderProcessing.exceptions.InvalidOrderStateException;
import com.rft.orderProcessing.exceptions.OrderCreationException;
import com.rft.orderProcessing.exceptions.OrderNotFoundException;
import com.rft.orderProcessing.messaging.OrderCreatedEvent;
import com.rft.orderProcessing.messaging.OrderEventPublisher;
import com.rft.orderProcessing.models.Order;
import com.rft.orderProcessing.models.OrderResponse;
import com.rft.orderProcessing.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public OrderResponse createOrder(Order orderRequest) {

        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new OrderCreationException("Order must contain at least one item");
        }

        try {

            com.rft.orderProcessing.domain.Order order = new com.rft.orderProcessing.domain.Order(
                    orderRequest.getCustomerId(),
                    orderRequest.getItems().stream()
                            .map(item -> new OrderItem(item.getSku(), item.getQuantity()))
                            .toList());

            com.rft.orderProcessing.domain.Order savedOrder = orderRepository.save(order);

            eventPublisher.publish(
                    new OrderCreatedEvent(savedOrder.getOrderId().toString()));

            return new OrderResponse(
                    savedOrder.getOrderId().toString(),
                    savedOrder.getStatus().toString());

        } catch (Exception ex) {
            throw new OrderCreationException("Failed to create order");
        }
    }

    public OrderResponse getOrder(Long id) {

        com.rft.orderProcessing.domain.Order order = getOrderById(id);

        return new OrderResponse(order.getOrderId().toString(), order.getStatus().toString());
    }

    public OrderResponse updateOrderState(Long id, String request) {

        if (!isValidState(request)) {
            throw new InvalidOrderStateException(request);
        }

        com.rft.orderProcessing.domain.Order order = getOrderById(id);
        order.setStatus(OrderState.valueOf(request.toUpperCase()));
        orderRepository.save(order);

        return new OrderResponse(order.getOrderId().toString(), order.getStatus().toString());
    }

    private com.rft.orderProcessing.domain.Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private boolean isValidState(String state) {

        try {

            OrderState.valueOf(state.toUpperCase());
            return true;

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
