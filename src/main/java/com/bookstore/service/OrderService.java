package com.bookstore.service;

import com.bookstore.dto.order.OrderRequestDto;
import com.bookstore.dto.order.OrderResponseDto;
import com.bookstore.dto.order.UpdateOrderStatusRequestDto;
import java.util.List;

public interface OrderService {

    OrderResponseDto placeOrder(OrderRequestDto requestDto);

    List<OrderResponseDto> getOrderHistory();

    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);

    List<OrderResponseDto> getAllOrdersForAdmin();
}
