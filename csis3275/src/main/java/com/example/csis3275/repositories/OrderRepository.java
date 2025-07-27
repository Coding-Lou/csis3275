package com.example.csis3275.repositories;

import com.example.csis3275.entities.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findOrdersByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.orderStatus = 'PAID', o.paymentType = 'CREDIT_CARD' WHERE o.orderId = :orderId")
    void updateOrderStatus(@Param("orderId") Long orderId, String paymentType);

}
