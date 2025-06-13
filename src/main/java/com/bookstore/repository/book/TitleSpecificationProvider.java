package com.bookstore.repository.book;

import com.bookstore.model.Book;
import com.bookstore.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component("title")
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    public static final String KEY = "title";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Book> getSpecification(List<String> titles) {
        return (root, query, cb) -> {
            CriteriaBuilder.In<String> predicate = cb.in(root.get("title"));
            titles.forEach(predicate::value);
            return predicate;
        };
    }
}
