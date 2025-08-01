package com.bookstore.service;

import com.bookstore.dto.order.OrderItemResponseDto;
import java.util.List;

public interface OrderItemService {
    List<OrderItemResponseDto> getAllByOrderId(Long orderId);

    OrderItemResponseDto getByOrderIdAndItemId(Long orderId, Long itemId);
}
