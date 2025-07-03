package com.bookstore.mapper;

import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CreateCategoryDto;
import com.bookstore.model.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toCategory(CreateCategoryDto category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromDto(CreateCategoryDto dto, @MappingTarget Category category);
}
