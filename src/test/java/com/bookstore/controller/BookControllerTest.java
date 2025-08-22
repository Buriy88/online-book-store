package com.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.CreateBookRequestDto;
import com.bookstore.model.Category;
import com.bookstore.repository.CategoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class BookControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long categoryId;

    @BeforeEach
    @Sql(scripts = "classpath:database/books/insert-three-books.sql")
    void setUp() {
        Category cat = new Category();
        cat.setName("Test Category");
        cat.setDescription("Desc");
        categoryId = categoryRepository.save(cat).getId();
    }

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/insert-three-books.sql"));
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-books.sql")
            );
        }
    }

    @Test
    @DisplayName("Get all books")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllBooks_ValidRequest() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto()
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
                ))
        );

        expected.add(new BookDto()
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
                ))
        );

        expected.add(new BookDto()
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
                ))
        );

        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(responseJson);
        BookDto[] actualBookDto = objectMapper.readValue(
                root.get("content").toString(),
                BookDto[].class
        );

        assertEquals(3, actualBookDto.length);
        assertEquals(expected, Arrays.stream(actualBookDto).toList());

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /books - \"Create a new book\"")
    void createBook_ValidRequest() throws Exception {

        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto()
                .setTitle("title")
                .setAuthor("author")
                .setIsbn("1234567891")
                .setPrice(BigDecimal.valueOf(100))
                .setDescription("description")
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(categoryId));

        String json = objectMapper.writeValueAsString(createBookRequestDto);

        System.out.printf("json: %s", json);
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actualBookDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actualBookDto);
        assertNotNull(actualBookDto.getId());
        assertEquals(createBookRequestDto.getTitle(), actualBookDto.getTitle());
        assertEquals(createBookRequestDto.getAuthor(), actualBookDto.getAuthor());
        BookDto bookDto = new BookDto()
                .setTitle(createBookRequestDto.getTitle())
                .setAuthor(createBookRequestDto.getAuthor())
                .setIsbn(createBookRequestDto.getIsbn())
                .setPrice(createBookRequestDto.getPrice())
                .setDescription(createBookRequestDto.getDescription())
                .setCoverImage(createBookRequestDto.getCoverImage());
        EqualsBuilder.reflectionEquals(bookDto, actualBookDto, "id,CategoryIds");

    }

    @Test
    @DisplayName("GET /books/{id} - Get book by ID")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getBookById_ValidId_ReturnsBook() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);

        assertEquals(1L, actual.getId());
        assertEquals("The Hobbit", actual.getTitle());
        assertEquals("J.R.R. Tolkien", actual.getAuthor());
        assertEquals("978-0547928227", actual.getIsbn());
        assertEquals(BigDecimal.valueOf(15.99), actual.getPrice());
        assertEquals("Fantasy novel", actual.getDescription());
    }

    @Test
    @DisplayName("PUT /books/{id} - Update book")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_ValidRequest() throws Exception {
        CreateBookRequestDto updateDto = new CreateBookRequestDto()
                .setTitle("Updated Title")
                .setAuthor("Updated Author")
                .setIsbn("9876543248")
                .setPrice(BigDecimal.valueOf(55.55))
                .setDescription("Updated description")
                .setCoverImage("updated-cover.jpg")
                .setCategoryIds(Set.of(2L));

        String json = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(
                        put("/books/{id}", 2L)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);

        assertEquals("Updated Title", actual.getTitle());
        assertEquals("Updated Author", actual.getAuthor());
        assertEquals("9876543248", actual.getIsbn());
        assertEquals(BigDecimal.valueOf(55.55), actual.getPrice());
        assertEquals("Updated description", actual.getDescription());
    }

    @Test
    @DisplayName("DELETE /books/{id} - Delete book")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_ValidId_RemovesBook() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /books/search - Search books by params")
    @WithMockUser(username = "user", roles = "USER")
    void searchBooks_ByAuthor_ReturnsFilteredBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("authors", "J.R.R. Tolkien")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto[].class
        );

        assertEquals(1, books.length);
        assertEquals("The Hobbit", books[0].getTitle());
        assertEquals("J.R.R. Tolkien", books[0].getAuthor());
    }

}
