package com.bookstore.service;

import com.bookstore.dto.BookDtoWithoutCategoryIds;
import com.bookstore.dto.CategoryDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    List<CategoryDto> findAll();

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto dto);

    CategoryDto update(Long id, CategoryDto dto);

    void deleteById(Long id);

    List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id);
}
