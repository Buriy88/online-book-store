package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.BookSearchParametersDto;
import com.bookstore.repository.BookSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder specBuilder;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + id
                        + " not found"));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + id
                        + " not found"));
        bookRepository.delete(book);
    }

    @Override
    public BookDto updateBook(Long id, CreateBookRequestDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id " + id
                        + " not found"));
        bookMapper.updateBookFromDto(dto, book);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public List<Book> searchBooks(BookSearchParametersDto params) {
        Map<String, List<String>> paramMap = new HashMap<>();
        if (params.titles() != null && !params.titles().isEmpty()) {
            paramMap.put("title", params.titles());
        }
        if (params.authors() != null && !params.authors().isEmpty()) {
            paramMap.put("author", params.authors());
        }
        if (params.isbns() != null && !params.isbns().isEmpty()) {
            paramMap.put("isbn", params.isbns());
        }
        return bookRepository.findAll(specBuilder.build(paramMap));
    }

    @Override
    public BookDto mapToDto(Book book) {
        return bookMapper.toDto(book);
    }
}
