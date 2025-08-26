package com.bookstore.service;

import static com.bookstore.util.TestConstants.BOOK_AUTHOR;
import static com.bookstore.util.TestConstants.BOOK_COVER_IMAGE;
import static com.bookstore.util.TestConstants.BOOK_DESCRIPTION;
import static com.bookstore.util.TestConstants.BOOK_INVALID_ID;
import static com.bookstore.util.TestConstants.BOOK_ISBN;
import static com.bookstore.util.TestConstants.BOOK_PRICE;
import static com.bookstore.util.TestConstants.BOOK_TITLE;
import static com.bookstore.util.TestConstants.BOOK_VALID_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.CreateBookRequestDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.BookSearchParametersDto;
import com.bookstore.repository.BookSpecificationBuilder;
import com.bookstore.repository.CategoryRepository;
import com.bookstore.util.TestConstants;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private BookSpecificationBuilder specBuilder;

    private CreateBookRequestDto requestDto;
    private Book mappedBook;
    private Book savedBook;
    private BookDto mappedDto;
    private Category category;

    @BeforeEach
    void setUp() {
        requestDto = new CreateBookRequestDto();
        requestDto.setTitle(BOOK_TITLE);
        requestDto.setAuthor(BOOK_AUTHOR);
        requestDto.setIsbn(BOOK_ISBN);
        requestDto.setCoverImage(BOOK_COVER_IMAGE);
        requestDto.setPrice(BOOK_PRICE);
        requestDto.setDescription(BOOK_DESCRIPTION);
        requestDto.setCategoryIds(Set.of(1L));

        category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        mappedBook = new Book();
        mappedBook.setTitle(BOOK_TITLE);
        mappedBook.setAuthor(BOOK_AUTHOR);
        mappedBook.setIsbn(BOOK_ISBN);
        mappedBook.setCoverImage(BOOK_COVER_IMAGE);
        mappedBook.setPrice(BOOK_PRICE);
        mappedBook.setDescription(TestConstants.BOOK_DESCRIPTION);

        savedBook = new Book();
        savedBook.setId(100L);
        savedBook.setTitle(BOOK_TITLE);
        savedBook.setAuthor(BOOK_AUTHOR);
        savedBook.setIsbn(BOOK_ISBN);
        savedBook.setCategories(Set.of(category));

        mappedDto = new BookDto();
        mappedDto.setId(100L);
        mappedDto.setTitle(BOOK_TITLE);
        mappedDto.setAuthor(BOOK_AUTHOR);
        mappedDto.setIsbn(BOOK_ISBN);
        mappedDto.setCoverImage(BOOK_COVER_IMAGE);
        mappedDto.setPrice(BOOK_PRICE);
        mappedDto.setDescription(BOOK_DESCRIPTION);
    }

    @Test
    @DisplayName("getBookById_BookExists_ReturnsBookDto")
    public void getBookWithValidId_ShouldReturnBook() {
        Book newBook = new Book();
        newBook.setId(BOOK_VALID_ID);
        newBook.setAuthor(BOOK_AUTHOR);
        newBook.setTitle(BOOK_TITLE);
        newBook.setIsbn(BOOK_ISBN);
        newBook.setCoverImage(BOOK_COVER_IMAGE);
        newBook.setPrice(BOOK_PRICE);
        newBook.setDescription(BOOK_DESCRIPTION);
        newBook.setDeleted(false);
        newBook.setCategories(null);
        when(bookRepository.findById(BOOK_VALID_ID)).thenReturn(Optional.of(newBook));
        BookDto actualBook = bookService.getBookById(BOOK_VALID_ID);
        BookDto expectedBook = bookMapper.toDto(newBook);
        assertEquals(expectedBook, actualBook);
    }

    @Test
    @DisplayName("getBookById_BookDoesNotExist_ThrowsException")
    public void getBookWithInvalidId_ShouldThrowException() {
        when(bookRepository.findById(BOOK_INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> bookService.getBookById(BOOK_INVALID_ID));
        String expectedMessage = "Book with id " + BOOK_INVALID_ID + " not found";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("createBook_ValidRequest_ReturnsBookDto")
    void createBook_ValidRequest_ReturnsBookDto() {

        when(bookMapper.toModel(requestDto)).thenReturn(mappedBook);
        when(bookRepository.save(mappedBook)).thenReturn(savedBook);
        when(bookMapper.toDto(any(Book.class))).thenReturn(mappedDto);
        when(categoryService.getEntityById(1L)).thenReturn(category);

        BookDto result = bookService.createBook(requestDto);

        assertNotNull(result);
        assertEquals(BOOK_TITLE, result.getTitle());
        assertEquals(BOOK_AUTHOR, result.getAuthor());
        assertEquals(BOOK_ISBN, result.getIsbn());

        verify(bookMapper).toModel(requestDto);
        verify(bookRepository).save(mappedBook);
        verify(bookMapper).toDto(any(Book.class));
    }

    @Test
    @DisplayName("createBook_InvalidCategoryIds_ThrowsException")
    void createBook_InvalidCategoryIds_ThrowsException() {
        requestDto.setCategoryIds(Set.of(999L));
        BookServiceImpl spyBookService = Mockito.spy(
                new BookServiceImpl(bookRepository, bookMapper, specBuilder, categoryService)
        );
        when(bookMapper.toModel(requestDto)).thenReturn(mappedBook);

        doThrow(new EntityNotFoundException("Category not found"))
                .when(spyBookService)
                .getCategoriesFromIds(requestDto.getCategoryIds());

        assertThrows(EntityNotFoundException.class,
                () -> spyBookService.createBook(requestDto));

        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBook_EmptyCategoryList_SavesWithoutCategories")
    void createBook_EmptyCategoryList_SavesWithoutCategories() {
        requestDto.setCategoryIds(Collections.emptySet());

        when(bookMapper.toModel(requestDto)).thenReturn(mappedBook);
        when(bookRepository.save(mappedBook)).thenReturn(mappedBook);
        when(bookMapper.toDto(mappedBook)).thenReturn(mappedDto);

        BookDto result = bookService.createBook(requestDto);

        assertEquals(mappedDto, result);
        assertTrue(mappedBook.getCategories().isEmpty());
    }

    @Test
    @DisplayName("createBook_NoCategories_ReturnsBookDtoWithEmptyCategories")
    void createBook_NoCategories_ReturnsBookDtoWithEmptyCategories() {
        requestDto.setCategoryIds(Collections.emptySet());

        mappedBook.setCategories(Collections.emptySet());
        savedBook.setCategories(Collections.emptySet());

        when(bookMapper.toModel(requestDto)).thenReturn(mappedBook);
        when(bookRepository.save(mappedBook)).thenReturn(savedBook);
        when(bookMapper.toDto(any(Book.class))).thenReturn(mappedDto);

        BookDto result = bookService.createBook(requestDto);

        assertNotNull(result);
        verify(bookRepository).save(mappedBook);
        assertTrue(savedBook.getCategories().isEmpty());
    }

    @Test
    @DisplayName("findAll_WithResults_ReturnsPageOfBookDto")
    void findAll_WithResults_ReturnsPageOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(mappedBook));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(mappedBook)).thenReturn(mappedDto);

        Page<BookDto> result = bookService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(mappedDto, result.getContent().get(0));
    }

    @Test
    @DisplayName("findAll_NoResults_ReturnsEmptyPage")
    void findAll_NoResults_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = Page.empty();

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<BookDto> result = bookService.findAll(pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deleteBook_ExistingId_DeletesBook")
    void deleteBook_ExistingId_DeletesBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mappedBook));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(mappedBook);
    }

    @Test
    @DisplayName("deleteBook_NotExistingId_ThrowsException")
    void deleteBook_NotExistingId_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> bookService.deleteBook(1L));
        String expectedMessage = "Book with id " + 1L + " not found";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("updateBook_ValidRequest_ReturnsUpdatedBookDto")
    void updateBook_ValidRequest_ReturnsUpdatedBookDto() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mappedBook));
        when(bookRepository.save(any(Book.class))).thenReturn(mappedBook);
        when(bookMapper.toDto(mappedBook)).thenReturn(mappedDto);

        BookDto result = bookService.updateBook(1L, requestDto);

        assertEquals(mappedDto, result);
    }

    @Test
    @DisplayName("updateBook_NotExistingId_ThrowsException")
    void updateBook_NotExistingId_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(1L, requestDto));
    }

    @Test
    @DisplayName("searchBooks_WithParams_ReturnsResults")
    void searchBooks_WithParams_ReturnsResults() {
        BookSearchParametersDto params =
                new BookSearchParametersDto(
                        List.of("Test"), null, null);

        when(bookRepository.findAll(
                Mockito.<Specification<Book>>any())).thenReturn(List.of(mappedBook));
        when(bookMapper.toDto(mappedBook)).thenReturn(mappedDto);

        List<Book> result = bookService.searchBooks(params);
        List<BookDto> resultDto = result
                .stream()
                .map(bookMapper::toDto)
                .toList();

        assertEquals(1, resultDto.size());
        assertEquals(mappedDto, resultDto.get(0));
    }
}
