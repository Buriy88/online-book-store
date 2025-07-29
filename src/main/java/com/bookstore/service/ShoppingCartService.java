package com.bookstore.service;

import com.bookstore.dto.ShoppingCartResponseDto;
import com.bookstore.dto.cart.CartItemRequestDto;
import com.bookstore.dto.cart.CartItemUpdateRequestDto;
import com.bookstore.model.User;

public interface ShoppingCartService {

    ShoppingCartResponseDto getCartForCurrentUser();

    ShoppingCartResponseDto addItemToCart(CartItemRequestDto requestDto);

    ShoppingCartResponseDto updateCartItemQuantity(Long cartItemId,
                                               CartItemUpdateRequestDto requestDto);

    void removeCartItem(Long cartItemId);

    void createCartForUser(User user);

}
