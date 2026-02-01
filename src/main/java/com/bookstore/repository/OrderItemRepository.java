package com.bookstore.repository;

import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    List<OrderItem> findAllByOrder(Order order);

    Optional<OrderItem> findByIdAndOrderId(Long id, Long orderId);
}
