package com.bookstore.repository;

import com.bookstore.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book>{
    private final SpecificationProviderManager manager;

@Override
    public Specification<Book> build(Map<String, List<String>> params) {
        Specification<Book> spec = Specification.where(null);
        for (var entry : params.entrySet()) {
            spec = spec.and(manager.getSpecification(entry.getKey(), entry.getValue()));
        }
        return spec;
    }
}
