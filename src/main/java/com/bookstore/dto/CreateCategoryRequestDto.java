package com.bookstore.dto;

import com.bookstore.model.Category;
import java.util.Set;

public class CreateCategoryRequestDto {
    private String name;
    private String description;
    private Set<Category> categories;
}
