package com.bookstore.service;

import com.bookstore.dto.BookDtoWithoutCategoryIds;
import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CreateCategoryDto;
import java.util.List;

public interface CategoryService {

    List<CategoryDto> findAll();

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryDto dto);

    CategoryDto update(Long id, CreateCategoryDto dto);

    void deleteById(Long id);

    List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id);
}
