package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Map;

public interface SpecificationBuilder <T>{
    Specification<Book> build(Map<String, List<String>> params);
}
