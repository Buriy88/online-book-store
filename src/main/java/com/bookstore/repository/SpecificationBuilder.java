package com.bookstore.repository;

import com.bookstore.model.Book;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<Book> build(Map<String, List<String>> params);
}
