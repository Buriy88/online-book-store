package com.bookstore;

import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.book.BookDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public final class TestUtil {

    private TestUtil() {
    }

    public static BookDto createHobbitBook() {
        return new BookDto()
                .setId(1L)
                .setTitle("The Hobbit")
                .setAuthor("J.R.R. Tolkien")
                .setIsbn("978-0547928227")
                .setPrice(BigDecimal.valueOf(15.99))
                .setDescription("Fantasy novel")
                .setCoverImage(null)
                .setCategories(Set.of(
                        new CategoryDto()
                                .setId(1L)
                                .setName("Fiction")
                                .setDescription("Fictional books")
                ));
    }

    public static BookDto createBriefHistoryBook() {
        return new BookDto()
                .setId(2L)
                .setTitle("A Brief History of Time")
                .setAuthor("Stephen Hawking")
                .setIsbn("978-0553380163")
                .setPrice(BigDecimal.valueOf(18.50))
                .setDescription("Cosmology and science")
                .setCoverImage(null)
                .setCategories(Set.of(
                        new CategoryDto()
                                .setId(2L)
                                .setName("Science")
                                .setDescription("Scientific literature")
                ));
    }

    public static BookDto createCleanCodeBook() {
        return new BookDto()
                .setId(3L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("978-0132350884")
                .setPrice(BigDecimal.valueOf(30.00))
                .setDescription("A handbook of agile software craftsmanship")
                .setCoverImage(null)
                .setCategories(Set.of(
                        new CategoryDto()
                                .setId(3L)
                                .setName("Programming")
                                .setDescription("Books about software development")
                ));
    }

    public static List<BookDto> getAllTestBooks() {
        return List.of(
                createHobbitBook(),
                createBriefHistoryBook(),
                createCleanCodeBook()
        );
    }

    public static CategoryDto createFictionCategory() {
        return new CategoryDto()
                .setId(1L)
                .setName("Fiction")
                .setDescription("Fictional books");
    }

    public static CategoryDto createScienceCategory() {
        return new CategoryDto()
                .setId(2L)
                .setName("Science")
                .setDescription("Scientific literature");
    }

    public static CategoryDto createProgrammingCategory() {
        return new CategoryDto()
                .setId(3L)
                .setName("Programming")
                .setDescription("Books about software development");
    }

    public static List<CategoryDto> getAllTestCategories() {
        return List.of(
                createFictionCategory(),
                createScienceCategory(),
                createProgrammingCategory()
        );
    }
}
