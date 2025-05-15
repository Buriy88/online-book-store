package com.bookstore.repository.book;

import com.bookstore.model.Book;
import com.bookstore.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.List;

@Component("author")
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    public static final String KEY = "author";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Book> getSpecification(List<String> authors) {
        return (root, query, cb) -> {
            CriteriaBuilder.In<String> predicate = cb.in(root.get("author"));
            authors.forEach(predicate::value);
            return predicate;
        };
    }
}
