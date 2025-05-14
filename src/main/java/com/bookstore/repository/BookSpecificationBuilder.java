package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class BookSpecificationBuilder {

    @Autowired
    private SpecificationProviderManager manager;

    public Specification<Book> build(Map<String, List<String>> params) {
        Specification<Book> spec = Specification.where(null);

        for (var entry : params.entrySet()) {
            spec = spec.and(manager.getSpecification(entry.getKey(), entry.getValue()));
        }

        return spec;
    }
}
