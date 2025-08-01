package com.bookstore.dto.order;

import com.bookstore.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDto {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}
