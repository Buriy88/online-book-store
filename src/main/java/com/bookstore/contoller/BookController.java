package com.bookstore.contoller;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.model.Book;
import com.bookstore.repository.BookSearchParametersDto;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    @Autowired
    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto dto) {
        return bookService.createBook(dto);
    }

    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @RequestBody CreateBookRequestDto dto) {
        return bookService.updateBook(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @GetMapping("/search")
    public List<BookDto> searchBooks(BookSearchParametersDto params) {
        List<Book> books = bookService.searchBooks(params);
        return books.stream()
                .map(bookService::mapToDto)
                .toList();
    }
}
