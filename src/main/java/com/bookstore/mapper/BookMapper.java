package com.bookstore.mapper;

import com.bookstore.DTO.BookDto;
import com.bookstore.DTO.CreateBookRequestDto;
import com.bookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);
    Book toModel(CreateBookRequestDto dto);
}
