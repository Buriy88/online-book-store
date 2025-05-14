package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.model.Book;
import com.bookstore.repository.BookSearchParametersDto;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    BookDto getBookById(Long id);

    List<BookDto> findAll();

    void deleteBook(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto dto);

    List<Book> searchBooks(BookSearchParametersDto params);

    BookDto mapToDto(Book book);

}
