package com.bookstore.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(exclude = "id")
public class CategoryDto {
    private Long id;

    private String name;

    private String description;
}
