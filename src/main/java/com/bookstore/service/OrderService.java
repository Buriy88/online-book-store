package com.bookstore.service;

import com.bookstore.dto.order.OrderItemResponseDto;
import com.bookstore.dto.order.OrderRequestDto;
import com.bookstore.dto.order.OrderResponseDto;
import com.bookstore.dto.order.UpdateOrderStatusRequestDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponseDto placeOrder(OrderRequestDto requestDto);

    Page<OrderResponseDto> getOrderHistory(Pageable pageable);

    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);

    List<OrderItemResponseDto> getAllByOrderId(Long orderId);

    OrderItemResponseDto getByOrderIdAndItemId(Long orderId, Long itemId);
}
