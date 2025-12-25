package com.rft.orderProcessing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rft.orderProcessing.exceptions.InvalidOrderStateException;
import com.rft.orderProcessing.exceptions.OrderCreationException;
import com.rft.orderProcessing.exceptions.OrderNotFoundException;
import com.rft.orderProcessing.models.Item;
import com.rft.orderProcessing.models.Order;
import com.rft.orderProcessing.models.OrderResponse;
import com.rft.orderProcessing.models.OrderUpdateRequest;
import com.rft.orderProcessing.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderProcessingController.class)
public class OrderProcessingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_returns202Accepted() throws Exception {

        OrderResponse mockResponse = new OrderResponse("123", "PENDING");

        Order newOrderRequest = new Order(
                "123",
                List.of(
                        new Item("ABC", "2"),
                        new Item("DEF", "4")));

        Mockito.when(orderService.createOrder(Mockito.any(Order.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(
                post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrderRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.orderId").value("123"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        Mockito.verify(orderService).createOrder(Mockito.any(Order.class));
    }

    @Test
    void createOrder_OrderRequestIsEmpty() throws Exception {

        List<Item> emptyItemList = new ArrayList<>();
        Order newOrderRequest = new Order("123", emptyItemList);

        Mockito.when(orderService.createOrder(Mockito.any(Order.class)))
                .thenThrow(new OrderCreationException("Order must contain at least one item"));

        mockMvc.perform(
                post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Order must contain at least one item"));
    }

    @Test
    void getOrderStatus_OrderFound() throws Exception {

        Long orderId = 1L;

        OrderResponse mockResponse = new OrderResponse("1", "PENDING");

        Mockito.when(orderService.getOrder(orderId))
                .thenReturn(mockResponse);

        mockMvc.perform(
                get("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrderStatus_OrderNotFound() throws Exception {

        Long orderId = 1L;

        Mockito.when(orderService.getOrder(orderId))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(
                get("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Order not found with orderId: 1"));
    }

    @Test
    void updateOrderState_UpdateSuccess() throws Exception {

        Long orderId = 1L;

        OrderUpdateRequest request = new OrderUpdateRequest("PAID");

        OrderResponse mockResponse = new OrderResponse("1", "PAID");

        Mockito.when(orderService.updateOrderState(orderId, "PAID"))
                .thenReturn(mockResponse);

        mockMvc.perform(
                patch("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("1"))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void updateOrderStatus_InvalidState() throws Exception {

        Long orderId = 1L;

        OrderUpdateRequest request = new OrderUpdateRequest("CANCELLED");

        Mockito.when(orderService.updateOrderState(orderId, "CANCELLED"))
                .thenThrow(new InvalidOrderStateException("CANCELLED"));

        mockMvc.perform(
                patch("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("CANCELLED is not a proper order state"));
    }
}
