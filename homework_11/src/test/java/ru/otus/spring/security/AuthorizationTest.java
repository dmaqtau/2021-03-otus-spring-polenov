package ru.otus.spring.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.spring.config.TestSpringSecurityConfig;
import ru.otus.spring.controller.BookController;
import ru.otus.spring.controller.PageController;
import ru.otus.spring.service.BookService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.spring.config.TestSpringSecurityConfig.ADMIN_LOGIN;
import static ru.otus.spring.config.TestSpringSecurityConfig.COMMON_USER_LOGIN;
import static ru.otus.spring.config.TestSpringSecurityConfig.MANAGER_LOGIN;

@DisplayName("Тесты авторизации")
@Import({TestSpringSecurityConfig.class})
@WebMvcTest({PageController.class, BookController.class})
class AuthorizationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private static final Long BOOK_ID = 1L;

    @Test
    @WithUserDetails(ADMIN_LOGIN)
    @DisplayName("Проверка доступов для пользователя с ролью админа")
    void testAdminRole() throws Exception {
        verifyAllowsGet();
        verifyModifying(true);

        mockMvc.perform(get("/admin")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(MANAGER_LOGIN)
    @DisplayName("Проверка доступов для пользователя с ролью менеджера")
    void testManagerRole() throws Exception {
        verifyAllowsGet();
        verifyModifying(true);

        mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(COMMON_USER_LOGIN)
    @DisplayName("Проверка доступов для пользователя с ролью обычного пользователя")
    void testUserRole() throws Exception {
        verifyAllowsGet();
        verifyModifying(false);

        mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    private void verifyAllowsGet() throws Exception {
        mockMvc.perform(get("/about")).andExpect(status().isOk());
        mockMvc.perform(get("/api/book")).andExpect(status().isOk());
        mockMvc.perform(get("/api/book/" + BOOK_ID)).andExpect(status().isOk());
    }

    private void verifyModifying(boolean isAllow) throws Exception {
        mockMvc.perform(delete("/api/book/" + BOOK_ID)).andExpect(isAllow? status().isOk(): status().isForbidden());

        mockMvc.perform(put("/api/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(AuthenticationTest.getBookDTOJson(BOOK_ID)))
                .andExpect(isAllow? status().isOk(): status().isForbidden());

        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(AuthenticationTest.getBookDTOJson(null)))
                .andExpect(isAllow? status().isOk(): status().isForbidden());
    }
}
