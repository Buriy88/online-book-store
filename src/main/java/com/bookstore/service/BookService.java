package com.bookstore.service;

import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.CreateBookRequestDto;
import com.bookstore.model.Book;
import com.bookstore.repository.BookSearchParametersDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    BookDto getBookById(Long id);

    Page<BookDto> findAll(Pageable pageable);

    void deleteBook(Long id);

    BookDto updateBook(Long id, CreateBookRequestDto dto);

    List<Book> searchBooks(BookSearchParametersDto params);

    BookDto mapToDto(Book book);

}
