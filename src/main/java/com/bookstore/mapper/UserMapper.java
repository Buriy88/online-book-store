package com.bookstore.mapper;

import com.bookstore.dto.UserRegistrationRequestDto;
import com.bookstore.dto.UserResponseDto;
import com.bookstore.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    UserResponseDto toDto(User user);
}
