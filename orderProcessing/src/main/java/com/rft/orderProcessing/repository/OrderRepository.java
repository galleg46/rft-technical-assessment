package com.rft.orderProcessing.repository;

import com.rft.orderProcessing.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
