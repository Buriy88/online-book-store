package com.bookstore.controller;

import com.bookstore.dto.order.OrderItemResponseDto;
import com.bookstore.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders/{orderId}/items")
@Tag(name = "Order Items",
        description = "Endpoints for retrieving items within an order")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all order items",
            description = "Retrieves all items for a specific order")
    public List<OrderItemResponseDto> getOrderItems(@PathVariable Long orderId) {
        return orderItemService.getAllByOrderId(orderId);
    }

    @GetMapping("/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get specific order item",
            description = "Retrieves a specific item from an order")
    public OrderItemResponseDto getOrderItem(@PathVariable Long orderId,
                                             @PathVariable Long itemId) {
        return orderItemService.getByOrderIdAndItemId(orderId, itemId);
    }

}
