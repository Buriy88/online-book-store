package com.bookstore.service;

import com.bookstore.DTO.BookDto;
import com.bookstore.DTO.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);
    BookDto getBookById(Long id);
    List<BookDto> findAll();
}
