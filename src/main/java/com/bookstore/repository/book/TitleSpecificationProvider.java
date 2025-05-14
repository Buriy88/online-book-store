package com.bookstore.repository.book;

import com.bookstore.model.Book;
import com.bookstore.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {


    @Override
    public String getKey() {
        return "title";
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
