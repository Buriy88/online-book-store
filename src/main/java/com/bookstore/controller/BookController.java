package com.bookstore.controller;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.model.Book;
import com.bookstore.repository.BookSearchParametersDto;
import com.bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Endpoints for managing books")
public class BookController {
    @Autowired
    private final BookService bookService;

    @Operation(
            summary = "Get paginated list of books",
            description = "Returns a paginated and sorted list of all books"
    )
    @GetMapping
    public Page<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }
    @Operation(summary = "Get book by ID", description = "Returns a single book by its ID")
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }
    @Operation(summary = "Create a new book", description = "Creates a new book based on the provided information")
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto dto) {
        return bookService.createBook(dto);
    }
    @Operation(summary = "Update an existing book", description = "Updates book information by ID")
    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @RequestBody @Valid CreateBookRequestDto dto) {
        return bookService.updateBook(id, dto);
    }
    @Operation(summary = "Delete a book", description = "Deletes the book with given ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
    @Operation(summary = "Search for books", description = "Search books by filter parameters")
    @GetMapping("/search")
    public List<BookDto> searchBooks(BookSearchParametersDto params) {
        List<Book> books = bookService.searchBooks(params);
        return books.stream()
                .map(bookService::mapToDto)
                .toList();
    }
}
