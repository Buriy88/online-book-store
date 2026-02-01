package com.bookstore.controller;

import com.bookstore.dto.order.OrderItemResponseDto;
import com.bookstore.dto.order.OrderRequestDto;
import com.bookstore.dto.order.OrderResponseDto;
import com.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Order Management", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Place an order",
            description = "Places an order using the current user's shopping cart")
    public OrderResponseDto placeOrder(@RequestBody @Valid OrderRequestDto requestDto) {
        return orderService.placeOrder(requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user's order history",
            description = "Retrieves all orders placed by the current user with pagination")
    public Page<OrderResponseDto> getOrderHistory(Pageable pageable) {
        return orderService.getOrderHistory(pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status",
            description = "Allows ADMIN to update the status of an order")
    public OrderResponseDto updateOrderStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all order items",
            description = "Retrieves all items for a specific order")
    public List<OrderItemResponseDto> getOrderItems(@PathVariable Long orderId) {
        return orderService.getAllByOrderId(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get specific order item",
            description = "Retrieves a specific item from an order")
    public OrderItemResponseDto getOrderItem(@PathVariable Long orderId,
                                             @PathVariable Long itemId) {
        return orderService.getByOrderIdAndItemId(orderId, itemId);
    }
}
