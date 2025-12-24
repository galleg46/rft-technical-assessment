package com.rft.orderProcessing.service;

import com.rft.orderProcessing.domain.Order;
import com.rft.orderProcessing.domain.OrderItem;
import com.rft.orderProcessing.domain.OrderState;
import com.rft.orderProcessing.exceptions.InvalidOrderStateException;
import com.rft.orderProcessing.exceptions.OrderCreationException;
import com.rft.orderProcessing.exceptions.OrderNotFoundException;
import com.rft.orderProcessing.messaging.OrderCreatedEvent;
import com.rft.orderProcessing.messaging.OrderEventPublisher;
import com.rft.orderProcessing.models.Item;
import com.rft.orderProcessing.models.OrderResponse;
import com.rft.orderProcessing.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_success() {

        Long orderId = 1L;

        com.rft.orderProcessing.models.Order newOrderRequest = new com.rft.orderProcessing.models.Order(
                "123",
                List.of(
                        new Item("ABC", "1"),
                        new Item("DEF", "4")
                ));

        Order savedOrder = new Order(
                "123",
                List.of(
                        new OrderItem("ABC", "1"),
                        new OrderItem("DEF", "4")
                ));
        savedOrder.setOrderId(orderId);

        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(savedOrder);

        OrderResponse response = orderService.createOrder(newOrderRequest);

        assertNotNull(response);
        assertEquals("1", response.getOrderId());
        assertEquals("PENDING", response.getStatus());

        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
        Mockito.verify(orderEventPublisher).publish(Mockito.any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_throwsException_whenItemListIsEmpty() {

        com.rft.orderProcessing.models.Order newOrderRequest = new com.rft.orderProcessing.models.Order(
                "123", List.of());

        OrderCreationException exception = assertThrows(
                OrderCreationException.class,
                () -> orderService.createOrder(newOrderRequest));

        assertEquals("Order must contain at least one item", exception.getMessage());

        Mockito.verifyNoInteractions(orderRepository);
        Mockito.verifyNoInteractions(orderEventPublisher);
    }

    @Test
    void creatOrder_throwsException_whenRepositoryFails() {

        com.rft.orderProcessing.models.Order newOrderRequest = new com.rft.orderProcessing.models.Order(
                "123",
                List.of(
                        new Item("ABC", "1"),
                        new Item("DEF", "3")
                ));

        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenThrow(new RuntimeException("Cannot connect to Database"));

        OrderCreationException exception = assertThrows(
                OrderCreationException.class,
                () -> orderService.createOrder(newOrderRequest));

        assertEquals("Failed to create order", exception.getMessage());

        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
        Mockito.verify(orderEventPublisher, Mockito.never()).publish(Mockito.any());
    }

    @Test
    void getOrder_orderExists() {

        Long orderId = 1L;

        OrderItem item1 = new OrderItem("ABC", "2");
        List<OrderItem> items = new ArrayList<>();
        items.add(item1);

        Order order = new Order(
                "123",
                List.of(
                        new OrderItem("ABC", "1"),
                        new OrderItem("DEF", "4")
                ));
        order.setOrderId(orderId);

        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrder(orderId);

        assertNotNull(response);
        assertEquals(OrderResponse.class, response.getClass());
        assertEquals("1", response.getOrderId());
        assertEquals("PENDING", response.getStatus());

        Mockito.verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrder_orderNotFound() {

        Long orderId = 1L;

        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId));

        Mockito.verify(orderRepository).findById(orderId);
    }

    @Test
    void updateOrderState_success() {

        Long orderId = 1L;
        String newState = "PAID";

        OrderItem item1 = new OrderItem("ABC", "2");
        List<OrderItem> items = new ArrayList<>();
        items.add(item1);

        Order order = new com.rft.orderProcessing.domain.Order("123", items);
        order.setOrderId(orderId);
        order.setStatus(OrderState.PENDING);

        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(order);

        OrderResponse response = orderService.updateOrderState(orderId, newState);

        assertNotNull(response);
        assertEquals("1", response.getOrderId());
        assertEquals("PAID", response.getStatus());

        Mockito.verify(orderRepository).save(order);
    }

    @Test
    void updateOrderState_invalidOrderState() {
        Long orderId = 1L;
        String invalidState = "CANCELLED";

        assertThrows(InvalidOrderStateException.class, () -> orderService.updateOrderState(orderId, invalidState));

        Mockito.verify(orderRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }
}
