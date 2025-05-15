package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class SpecificationProviderManager {

    private final Map<String, SpecificationProvider<Book>> providers;

    @Autowired
    public SpecificationProviderManager(Map<String, SpecificationProvider<Book>> providerMap) {
        this.providers = providerMap;
    }

    public Specification<Book> getSpecification(String key, List<String> values) {
        if (!providers.containsKey(key)) {
            throw new IllegalArgumentException("No provider found for key: " + key);
        }
        return providers.get(key).getSpecification(values);
    }
}
