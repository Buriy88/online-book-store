package com.bookstore.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class CategoryDto {
    private Long id;

    private String name;

    private String description;
}
