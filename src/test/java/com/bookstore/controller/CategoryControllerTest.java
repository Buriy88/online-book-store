package com.bookstore.controller;

import static com.bookstore.controller.BookControllerTest.mockMvc;
import static com.bookstore.util.TestConstants.CATEGORY_DESCRIPTION;
import static com.bookstore.util.TestConstants.CATEGORY_ID;
import static com.bookstore.util.TestConstants.CATEGORY_INVALID_ID;
import static com.bookstore.util.TestConstants.CATEGORY_NAME;
import static com.bookstore.util.TestUtil.createFictionCategory;
import static com.bookstore.util.TestUtil.createHorrorCategory;
import static com.bookstore.util.TestUtil.createUpdateCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.dto.CategoryDto;
import com.bookstore.dto.CreateCategoryDto;
import com.bookstore.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
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
        String json = objectMapper.writeValueAsString(createCategoryDto);

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto expected = new CategoryDto()
                .setName(createCategoryDto.getName())
                .setDescription(createCategoryDto.getDescription());
        CategoryDto actual = objectMapper
                .readValue(
                        result.getResponse().getContentAsString(), CategoryDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /categories - Create category")
    void createCategory_ValidRequest() throws Exception {
        CreateCategoryDto createCategoryDto = createHorrorCategory();

        String json = objectMapper.writeValueAsString(createCategoryDto);

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto expected = new CategoryDto()
                .setName(createCategoryDto.getName())
                .setDescription(createCategoryDto.getDescription());

        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        assertEquals(expected, actual);

    }

    @Test
    @DisplayName("GET /categories - Get all categories")
    @WithMockUser(username = "user", roles = "USER")
    void getAllCategories_ValidRequest() throws Exception {
        List<CategoryDto> expected = TestUtil.getAllTestCategories();

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
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /categories/{id} - Get category by ID")
    void getCategoryById() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto expected = createFictionCategory();

        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("PUT /categories/{id} - Update category")
    void updateCategory() throws Exception {
        CreateCategoryDto updateDto = createUpdateCategory();
        String json = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", 2L)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto expected = new CategoryDto()
                .setName(updateDto.getName())
                .setDescription(updateDto.getDescription());
        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        assertEquals(expected, actual);
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

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /categories - Should return 400 when name is blank")
    void createCategory_InvalidName() throws Exception {
        CreateCategoryDto invalidDto = new CreateCategoryDto()
                .setName("")
                .setDescription(CATEGORY_DESCRIPTION);

        String json = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post("/categories")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /categories/{id} - Should return 404 when category not found")
    @WithMockUser(username = "user", roles = "USER")
    void getCategoryById_NotFound() throws Exception {
        mockMvc.perform(get("/categories/{id}", CATEGORY_INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("PUT /categories/{id} - Should return 404 when updating non-existing category")
    void updateCategory_NotFound() throws Exception {
        CreateCategoryDto updateDto = new CreateCategoryDto()
                .setName(CATEGORY_NAME)
                .setDescription(CATEGORY_DESCRIPTION);

        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/categories/{id}", CATEGORY_INVALID_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("DELETE /categories/{id} - Should return 404 when category not found")
    void deleteCategory_NotFound() throws Exception {
        mockMvc.perform(delete("/categories/{id}", CATEGORY_INVALID_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Should return 404 when category not found")
    void deleteCategory_Unauthorized() throws Exception {
        mockMvc.perform(delete("/categories/{id}", CATEGORY_ID))
                .andExpect((status().isUnauthorized()));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("DELETE /categories/{id} - Should return 404 when category not found")
    void deleteCategory_ForbiddenForUserRole() throws Exception {
        mockMvc.perform(delete("/categories/{id}", CATEGORY_ID))
                .andExpect((status().isForbidden()));
    }
}
