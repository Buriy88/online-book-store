package com.bookstore.service;

import com.bookstore.DTO.BookDto;
import com.bookstore.DTO.CreateBookRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id);
        if (book == null) {
            throw new EntityNotFoundException("Book with id " + id + " not found");
        }
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }
}
