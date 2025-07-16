package com.bookstore.service;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.BookSearchParametersDto;
import com.bookstore.repository.BookSpecificationBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    public static final String AUTHOR = "author";
    public static final String ISBN = "isbn";
    public static final String TITLE = "title";
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder specBuilder;
    private final CategoryService categoryService;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(categoryService::getEntityById)
                .collect(Collectors.toSet());

        book.setCategories(categories);

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
    public Page<BookDto> findAll(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(bookMapper::toDto);
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
            paramMap.put(TITLE, params.titles());
        }
        if (params.authors() != null && !params.authors().isEmpty()) {
            paramMap.put(AUTHOR, params.authors());
        }
        if (params.isbns() != null && !params.isbns().isEmpty()) {
            paramMap.put(ISBN, params.isbns());
        }
        Specification<Book> bookSpecification = specBuilder.build(paramMap);
        return bookRepository.findAll(bookSpecification);
    }

    @Override
    public BookDto mapToDto(Book book) {
        return bookMapper.toDto(book);
    }
}
