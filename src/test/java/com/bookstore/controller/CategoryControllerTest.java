package com.bookstore.controller;

import static com.bookstore.controller.BookControllerTest.mockMvc;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CreateCategoryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.builder.EqualsBuilder;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class CategoryControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext webApplicationContext,
            @Autowired DataSource dataSource
    ) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/insert-three-categories.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/delete-categories.sql")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /category - \"Create a new book\"")
    void category_ValidRequest() throws Exception {
        CreateCategoryDto createCategoryDto = new CreateCategoryDto()
                .setName("horror")
                .setDescription("This is a description");

        CategoryDto categoryDto = new CategoryDto()
                .setName(createCategoryDto.getName())
                .setDescription(createCategoryDto.getDescription());

        String json = objectMapper.writeValueAsString(createCategoryDto);
        System.out.printf("json: %s", json);

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /categories - Create category")
    void createCategory_ValidRequest() throws Exception {
        CreateCategoryDto createCategoryDto = new CreateCategoryDto()
                .setName("Horror")
                .setDescription("Scary stories");

        String json = objectMapper.writeValueAsString(createCategoryDto);

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("Horror");
        assertThat(actual.getDescription()).isEqualTo("Scary stories");
        EqualsBuilder.reflectionEquals(createCategoryDto, actual, "id");
    }

    @Test
    @DisplayName("GET /categories - Get all categories")
    @WithMockUser(username = "user", roles = "USER")
    void getAllCategories_ValidRequest() throws Exception {
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto()
                .setId(1L)
                .setName("Fiction")
                .setDescription("Fictional books")
        );
        expected.add(new CategoryDto()
                .setId(2L)
                .setName("Science")
                .setDescription("Scientific literature")
        );
        expected.add(new CategoryDto()
                .setId(3L)
                .setName("Programming")
                .setDescription("Books about software development")
        );

        MvcResult result = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        CategoryDto[] actualCategoryDto = objectMapper.readValue(
                responseJson,
                CategoryDto[].class
        );

        assertEquals(3, actualCategoryDto.length);
        assertEquals(expected, Arrays.stream(actualCategoryDto).toList());

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /categories/{id} - Get category by ID")
    void getCategoryById() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getName()).isEqualTo("Fiction");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("PUT /categories/{id} - Update category")
    void updateCategory() throws Exception {
        CreateCategoryDto updateDto = new CreateCategoryDto()
                .setName("Updated Name")
                .setDescription("Updated Description");

        String json = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", 2L)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(actual.getName()).isEqualTo("Updated Name");
        assertThat(actual.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("DELETE /categories/{id} - Delete category")
    void deleteCategory() throws Exception {
        mockMvc.perform(delete("/categories/{id}", 3L))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/categories/{id}", 3L))
                .andExpect(status().isNotFound());
    }
}
