package com.bookstore.controller;

import com.bookstore.dto.ShoppingCartResponseDto;
import com.bookstore.dto.cart.CartItemRequestDto;
import com.bookstore.dto.cart.CartItemUpdateRequestDto;
import com.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get current user's shopping cart",
            description = "Returns the shopping cart for the authenticated user. "
                    + "Available for USER and ADMIN.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ShoppingCartResponseDto getCart() {
        return shoppingCartService.getCartForCurrentUser();
    }

    @Operation(summary = "Add item to shopping cart",
            description = "Adds a book with the specified "
                    + "quantity to the user's shopping cart.")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponseDto addItemToCart(@RequestBody
                                                     @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addItemToCart(requestDto);
    }

    @Operation(summary = "Update cart item quantity",
            description = "Updates the quantity of a"
                    + " specific item in the shopping cart.")
    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ShoppingCartResponseDto updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody @Valid CartItemUpdateRequestDto requestDto
    ) {
        return shoppingCartService.updateCartItemQuantity(cartItemId, requestDto);
    }

    @Operation(summary = "Remove item from shopping cart",
            description = "Deletes a specific item from the shopping cart.")
    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(@PathVariable Long cartItemId) {
        shoppingCartService.removeCartItem(cartItemId);
    }
}
