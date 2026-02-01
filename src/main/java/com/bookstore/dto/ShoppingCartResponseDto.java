package com.bookstore.dto;

import com.bookstore.dto.cart.CartItemResponseDto;
import java.util.List;

public record ShoppingCartResponseDto(
        Long id,
        Long userId,
        List<CartItemResponseDto> cartItems
) {}
