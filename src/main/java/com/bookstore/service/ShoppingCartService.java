package com.bookstore.service;

import com.bookstore.dto.CartItemRequestDto;
import com.bookstore.dto.CartItemResponseDto;
import com.bookstore.dto.CartItemUpdateRequestDto;
import com.bookstore.dto.ShoppingCartResponseDto;
import com.bookstore.model.User;

public interface ShoppingCartService {

    ShoppingCartResponseDto getCartForCurrentUser();

    ShoppingCartResponseDto addItemToCart(CartItemRequestDto requestDto);

    CartItemResponseDto updateCartItemQuantity(Long cartItemId,
                                               CartItemUpdateRequestDto requestDto);

    void removeCartItem(Long cartItemId);

    void createCartForUser(User user);

}
