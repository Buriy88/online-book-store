package com.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CreateCategoryDto;
import com.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.model.Book;
import com.bookstore.model.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Test Category";
    private static final String CATEGORY_DESCRIPTION = "Test Description";
    private static final Long BOOK_ID = 10L;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private CreateCategoryDto createCategoryDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(CATEGORY_ID);
        category.setName(CATEGORY_NAME);
        category.setDescription(CATEGORY_DESCRIPTION);

        categoryDto = new CategoryDto();
        categoryDto.setId(CATEGORY_ID);
        categoryDto.setName(CATEGORY_NAME);
        categoryDto.setDescription(CATEGORY_DESCRIPTION);

        createCategoryDto = new CreateCategoryDto();
        createCategoryDto.setName(CATEGORY_NAME);
        createCategoryDto.setDescription(CATEGORY_DESCRIPTION);
    }

    @Test
    @DisplayName("findAll should return list of categories")
    void findAll_ReturnsList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.findAll();

        assertThat(result).hasSize(1).contains(categoryDto);
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("getById should return category when exists")
    void getCategoryByValidId_ReturnsCategory() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getById(CATEGORY_ID);

        assertThat(result).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("getById should throw exception when not found")
    void getById_WhenNotFound_Throws() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(CATEGORY_ID));
        String expectedMessage = "Category with id " + CATEGORY_ID + " not found";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    @DisplayName("getEntityById should return category when exists")
    void getEntityByValidId_ReturnsEntity() {
        when(categoryRepository
                .findById(CATEGORY_ID))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getEntityById(CATEGORY_ID);

        assertThat(result).isEqualTo(category);
    }

    @Test
    @DisplayName("save should map, save and return category dto")
    void save_ShouldReturnSavedDto() {
        when(categoryMapper
                .toCategory(createCategoryDto))
                .thenReturn(category);
        when(categoryRepository
                .save(category))
                .thenReturn(category);
        when(categoryMapper
                .toDto(category))
                .thenReturn(categoryDto);

        CategoryDto result = categoryService.save(createCategoryDto);

        assertThat(result).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("update should throw exception when not found")
    void update_WhenNotFound_Throws() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(CATEGORY_ID, createCategoryDto)
        );
        String expectedMessage = "Category with id " + CATEGORY_ID + " not found";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("deleteById should delete category when exists")
    void deleteById_WhenExists_Deletes() {
        when(categoryRepository
                .findById(CATEGORY_ID))
                .thenReturn(Optional.of(category));

        categoryService.deleteById(CATEGORY_ID);

        verify(categoryRepository).delete(category);
    }

    @Test
    @DisplayName("deleteById should throw exception when not found")
    void deleteById_WhenNotFound_Throws() {
        when(categoryRepository
                .findById(CATEGORY_ID))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.deleteById(CATEGORY_ID)
        );

        String expectedMessage = "Category with id " + CATEGORY_ID + " not found";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("getBooksByCategoryId should return list of books")
    void getBooksByCategoryId_ReturnsList() {
        Book book = new Book();
        book.setId(BOOK_ID);

        BookDtoWithoutCategoryIds dtoWithoutCategories = new BookDtoWithoutCategoryIds();
        dtoWithoutCategories.setId(BOOK_ID);

        when(bookRepository
                .findAllByCategories_Id(CATEGORY_ID))
                .thenReturn(List.of(book));
        when(bookMapper
                .toDtoWithoutCategories(book))
                .thenReturn(dtoWithoutCategories);

        List<BookDtoWithoutCategoryIds> result = categoryService.getBooksByCategoryId(CATEGORY_ID);

        assertThat(result).hasSize(1).contains(dtoWithoutCategories);
    }

}
