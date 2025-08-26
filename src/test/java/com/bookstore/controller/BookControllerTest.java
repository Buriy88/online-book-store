package com.bookstore.controller;

import static com.bookstore.util.TestConstants.BOOK_AUTHOR;
import static com.bookstore.util.TestConstants.BOOK_COVER_IMAGE;
import static com.bookstore.util.TestConstants.BOOK_DESCRIPTION;
import static com.bookstore.util.TestConstants.BOOK_INVALID_AUTHOR;
import static com.bookstore.util.TestConstants.BOOK_INVALID_ID;
import static com.bookstore.util.TestConstants.BOOK_INVALID_ISBN;
import static com.bookstore.util.TestConstants.BOOK_INVALID_PRICE;
import static com.bookstore.util.TestConstants.BOOK_INVALID_TITLE;
import static com.bookstore.util.TestConstants.BOOK_ISBN;
import static com.bookstore.util.TestConstants.BOOK_PRICE;
import static com.bookstore.util.TestConstants.BOOK_TITLE;
import static com.bookstore.util.TestUtil.createHobbitBook;
import static com.bookstore.util.TestUtil.getUpdateBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.dto.book.BookDto;
import com.bookstore.dto.book.CreateBookRequestDto;
import com.bookstore.util.TestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
@Sql(scripts = "classpath:database/books/insert-three-books.sql")
class BookControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        List<BookDto> expected = TestUtil.getAllTestBooks();

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

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /books - \"Create a new book\"")
    void createBook_ValidRequest() throws Exception {

        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto()
                .setTitle(BOOK_TITLE)
                .setAuthor(BOOK_AUTHOR)
                .setIsbn(BOOK_ISBN)
                .setPrice(BOOK_PRICE)
                .setDescription(BOOK_DESCRIPTION)
                .setCoverImage(BOOK_COVER_IMAGE)
                .setCategoryIds(Set.of(1L));

        String json = objectMapper.writeValueAsString(createBookRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actualBookDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actualBookDto);
        BookDto expected = new BookDto()
                .setTitle(createBookRequestDto.getTitle())
                .setAuthor(createBookRequestDto.getAuthor())
                .setIsbn(createBookRequestDto.getIsbn())
                .setPrice(createBookRequestDto.getPrice())
                .setDescription(createBookRequestDto.getDescription())
                .setCoverImage(createBookRequestDto.getCoverImage());
        assertEquals(expected.getTitle(), actualBookDto.getTitle());
        assertEquals(expected.getAuthor(), actualBookDto.getAuthor());
        assertEquals(expected.getIsbn(), actualBookDto.getIsbn());
        assertEquals(expected.getPrice(), actualBookDto.getPrice());
        assertEquals(expected.getDescription(), actualBookDto.getDescription());
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
        BookDto expected = createHobbitBook();

        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("PUT /books/{id} - Update book")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_ValidRequest() throws Exception {
        CreateBookRequestDto updateDto = getUpdateBook();

        String json = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(
                        put("/books/{id}", 2L)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);

        assertEquals(updateDto.getTitle(), actual.getTitle());
        assertEquals(updateDto.getAuthor(), actual.getAuthor());
        assertEquals(updateDto.getIsbn(), actual.getIsbn());
        assertEquals(updateDto.getPrice(), actual.getPrice());
        assertEquals(updateDto.getDescription(), actual.getDescription());
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
        BookDto expected = createHobbitBook();
        assertEquals(expected, books[0]);
    }

    @Test
    @DisplayName("POST /books - Invalid request (missing required fields)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto invalidBook = new CreateBookRequestDto()
                .setTitle(BOOK_INVALID_TITLE)
                .setIsbn(BOOK_INVALID_ISBN)
                .setPrice(BOOK_INVALID_PRICE);

        String json = objectMapper.writeValueAsString(invalidBook);

        mockMvc.perform(post("/books")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /books/{id} - Non-existing book")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getBookById_NotFound() throws Exception {
        mockMvc.perform(get("/books/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /books/{id} - Invalid update request")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateBook_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto invalidUpdate = new CreateBookRequestDto()
                .setTitle("")
                .setAuthor(BOOK_INVALID_AUTHOR)
                .setIsbn(BOOK_INVALID_ISBN)
                .setPrice(BOOK_INVALID_PRICE);

        String json = objectMapper.writeValueAsString(invalidUpdate);

        mockMvc.perform(put("/books/{id}", 1L)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /books/{id} - Non-existing book")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteBook_NotFound() throws Exception {
        mockMvc.perform(delete("/books/{id}", BOOK_INVALID_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /books/search - No results")
    @WithMockUser(username = "user", roles = "USER")
    void searchBooks_NoMatches_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search")
                        .param("authors", BOOK_INVALID_AUTHOR)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] books = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto[].class
        );

        assertEquals(0, books.length);
    }

    @Test
    @DisplayName("POST /books - Forbidden for USER role")
    @WithMockUser(username = "user", roles = "USER")
    void createBook_ForbiddenForUserRole() throws Exception {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto()
                .setTitle(BOOK_TITLE)
                .setAuthor(BOOK_AUTHOR)
                .setIsbn(BOOK_ISBN)
                .setPrice(BOOK_PRICE)
                .setDescription(BOOK_DESCRIPTION)
                .setCoverImage(BOOK_COVER_IMAGE)
                .setCategoryIds(Set.of(1L));

        String json = objectMapper.writeValueAsString(createBookRequestDto);
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("GET /books - Forbidden for unauthorized user")
    void getAllBooks_Unauthorized() throws Exception {
        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
