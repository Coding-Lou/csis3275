package com.example.csis3275.repositories;

import com.example.csis3275.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query
    List<Order> findOrdersByOrderId(Long orderId);

}