package com.example.csis3275.repositories;

import com.example.csis3275.entities.BookOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {
    @Query
    List<BookOrder> findBookOrdersByOrderId(Long orderId);

}
