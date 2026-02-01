package com.bookstore.service;

import com.bookstore.dto.BookDtoWithoutCategoryIds;
import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CreateCategoryDto;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.model.Category;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CategoryRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<CategoryDto> findAll() {
        List<Category> allCategory = categoryRepository.findAll();
        return allCategory.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id
                + " not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public Category getEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id
                        + " not found"));
    }

    @Override
    public CategoryDto save(CreateCategoryDto dto) {
        Category category = categoryMapper.toCategory(dto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id
                + " not found"));
        categoryMapper.updateCategoryFromDto(dto, category);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id
                + " not found"));
        categoryRepository.delete(category);
    }

    @Override
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id) {
        return bookRepository.findAllByCategories_Id(id).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

}
