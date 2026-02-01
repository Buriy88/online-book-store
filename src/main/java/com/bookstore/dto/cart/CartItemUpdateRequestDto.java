package com.bookstore.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateRequestDto {
    @Positive
    private int quantity;
}
