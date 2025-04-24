package com.bookstore.contoller;

import com.bookstore.DTO.BookDto;
import com.bookstore.DTO.CreateBookRequestDto;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    @Autowired
    private BookService bookService;

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
}
