package com.bookstore.mapper;

import com.bookstore.dto.order.OrderItemResponseDto;
import com.bookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    OrderItemResponseDto toDto(OrderItem orderItem);
}
