package com.bookstore.mapper;

import com.bookstore.dto.BookDto;
import com.bookstore.dto.BookDtoWithoutCategoryIds;
import com.bookstore.dto.CreateBookRequestDto;
import com.bookstore.model.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);
    
    Book toModel(CreateBookRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categories", ignore = true)
    void updateBookFromDto(CreateBookRequestDto dto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);
}
