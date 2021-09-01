package ru.otus.spring.security;

import java.util.List;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.spring.config.TestSpringSecurityConfig;
import ru.otus.spring.controller.BookController;
import ru.otus.spring.controller.PageController;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.dto.BookDTO;
import ru.otus.spring.service.BookService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.spring.config.TestSpringSecurityConfig.ADMIN_LOGIN;
import static ru.otus.spring.config.TestSpringSecurityConfig.ADMIN_PASS;
import static ru.otus.spring.config.TestSpringSecurityConfig.INACTIVE_USER_LOGIN;
import static ru.otus.spring.config.TestSpringSecurityConfig.INACTIVE_USER_PASS;

@DisplayName("Тесты аутентификации")
@Import({TestSpringSecurityConfig.class})
@WebMvcTest({PageController.class, BookController.class})
class AuthenticationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private static final Long NEW_BOOK_ID = 10L;
    private static final Long BOOK_ID_FIRST = 2L;
    private static final Long BOOK_ID_SECOND = 3L;

    private static final Long AUTHOR_ID = 1L;
    private static final Long GENRE_ID = 1L;
    private static final String BOOK_NAME = "test_book_name";
    private static final String BOOK_DESCRIPTION = "test_book_description";
    private static final String AUTHORIZATION = "Authorization";

    @BeforeEach
    void init(){
        Book existingBookFirst = new Book(BOOK_ID_FIRST, BOOK_NAME, new Author(AUTHOR_ID), new Genre(GENRE_ID), BOOK_DESCRIPTION);
        Book existingBookSecond = new Book(BOOK_ID_SECOND, BOOK_NAME, new Author(AUTHOR_ID), new Genre(GENRE_ID), BOOK_DESCRIPTION);

        when(bookService.create(any(BookDTO.class)))
            .thenAnswer((Answer<Book>) invocation -> {
                Object[] args = invocation.getArguments();
                BookDTO input = (BookDTO) args[0];

                return new Book(NEW_BOOK_ID, input.getName(), new Author(input.getAuthorId()), new Genre(input.getGenreId()), input.getDescription());
            });

        when(bookService.update(any(BookDTO.class)))
                .thenAnswer((Answer<Book>) invocation -> {
                    Object[] args = invocation.getArguments();
                    BookDTO input = (BookDTO) args[0];

                    return new Book(input.getId(), input.getName(), new Author(input.getAuthorId()), new Genre(input.getGenreId()), input.getDescription());
                });

        when(bookService.getAll()).thenReturn(List.of(existingBookFirst, existingBookSecond));
        when(bookService.findByID(BOOK_ID_FIRST)).thenReturn(existingBookFirst);
    }

    @DisplayName("Должно запретить запрос GET при попытке выполнения без токена или с неверным токеном")
    @ParameterizedTest
    @ValueSource(strings = {"/about", "/api/book", "/api/book/1"})
    void testForbidGetWithIncorrectToken(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isForbidden());

        assertThrowsOnWrongToken(get(path));
    }

    @DisplayName("Должно запретить запрос PUT при попытке выполнения без токена или с неверным токеном")
    @Test
    void testForbidPutWithIncorrectToken() throws Exception {
        mockMvc.perform(put("/api/book"))
                .andExpect(status().isForbidden());

        assertThrowsOnWrongToken(put("/api/book"));
    }

    @DisplayName("Должно запретить запрос POST при попытке выполнения без токена или с неверным токеном")
    @Test
    void testForbidPostWithIncorrectToken() throws Exception {
        mockMvc.perform(post("/api/book"))
                .andExpect(status().isForbidden());

        assertThrowsOnWrongToken(post("/api/book"));
    }

    @DisplayName("Должно запретить запрос DELETE при попытке выполнения без токена или с неверным токеном")
    @Test
    void testForbidDeleteWithIncorrectToken() throws Exception {
        mockMvc.perform(delete("/api/book/1"))
                .andExpect(status().isForbidden());

        assertThrowsOnWrongToken(delete("/api/book/1"));
    }
    
    @DisplayName("Должны успешно запросить токен и открыть страницы about и admin")
    @ParameterizedTest
    @ValueSource(strings = {"/about", "/admin"})
    void testOpenAboutPage(String path) throws Exception {
        mockMvc.perform(get(path)
                .header(AUTHORIZATION, getAuthString(ADMIN_LOGIN, ADMIN_PASS)))
                .andExpect(status().isOk());
    }
    

    @DisplayName("Должны успешно запросить токен и получить список книг")
    @Test
    void testGetTokenAndGetAllBooksForUser() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyUnauthorizedForWrongPass( get("/api/book"));

        MvcResult result = mockMvc.perform(get("/api/book")
                .header(AUTHORIZATION, getAuthString(ADMIN_LOGIN, ADMIN_PASS)))
                .andExpect(status().isOk())
                .andReturn();
        verifyGetBookList(result);
    }
    
    @DisplayName("Должны успешно запросить токен и получить книгу")
    @Test
    void testGetTokenAndGetBookForUser() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyUnauthorizedForWrongPass( get("/api/book/" + BOOK_ID_FIRST));

        MvcResult result = mockMvc.perform(get("/api/book/" + BOOK_ID_FIRST)
                .header(AUTHORIZATION, getAuthString(ADMIN_LOGIN, ADMIN_PASS)))
                .andExpect(status().isOk())
                .andReturn();
        verifyGetSingleBook(result);
    }

    @DisplayName("Должны успешно запросить токен и обновить книгу")
    @Test
    void testGetTokenAndUpdateBook() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyUnauthorizedForWrongPass( put("/api/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(BOOK_ID_FIRST)));

        MvcResult result = mockMvc.perform(put("/api/book")
                .header(AUTHORIZATION, getAuthString(ADMIN_LOGIN, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(BOOK_ID_FIRST)))
                .andExpect(status().isOk())
                .andReturn();

        verifyUpdateBookResult(result);
    }

    @DisplayName("Должны успешно запросить токен и создать книгу")
    @Test
    void testGetTokenAndCreateBook() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyUnauthorizedForWrongPass( post("/api/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(null)));

        MvcResult result = mockMvc.perform(post("/api/book")
                .header(AUTHORIZATION, getAuthString(ADMIN_LOGIN, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(null)))
                .andExpect(status().isOk()).andReturn();

        verifyCreateBookResult(result);
    }

    @WithUserDetails(ADMIN_LOGIN)
    @DisplayName("Должны успешно выполнить запрос DELETE для ранее авторизованного пользователя")
    @Test
    void testSuccessDeleteForAuthorizedUser() throws Exception {
        mockMvc.perform(delete("/api/book/1"))
                .andExpect(status().isOk());
    }

    @DisplayName("Должны успешно запросить токен и удалить книгу")
    @Test
    void testGetTokenAndDeleteBook() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyUnauthorizedForWrongPass( delete("/api/book/1"));

        mockMvc.perform(delete("/api/book/1")
                .header(AUTHORIZATION, getAuthString(ADMIN_LOGIN, ADMIN_PASS)))
                .andExpect(status().isOk());
    }

    @DisplayName("Не должны смочь получить токен для неактивного пользователя")
    @Test
    void testRejectAboutPageForInactiveUser() throws Exception {
        String token = getAuthString(INACTIVE_USER_LOGIN, INACTIVE_USER_PASS, false);
        assertThat(token).isBlank();
    }


    @WithUserDetails(ADMIN_LOGIN)
    @DisplayName("Должны получать ошибку 403 в случае несуществующей страницы")
    @Test
    void testNotFound() throws Exception {
        mockMvc.perform(get("/not/existing/path")
                .header(AUTHORIZATION, getAuthString(ADMIN_LOGIN, ADMIN_PASS)))
                .andExpect(status().isForbidden());
    }

    private static void verifyGetBookList(MvcResult result) throws Exception {
        MockHttpServletResponse response = result.getResponse();
        String responseJson = response.getContentAsString();

        List<Book> parsedBooks = new ObjectMapper().readValue(responseJson, new TypeReference<>() {});

        assertThat(parsedBooks)
                .hasSize(2)
                .anyMatch(
                        b -> BOOK_ID_FIRST.equals(b.getId()) &&
                                BOOK_NAME.equals(b.getName()) &&
                                BOOK_DESCRIPTION.equals(b.getDescription()) &&
                                AUTHOR_ID.equals(b.getAuthor().getId()) &&
                                GENRE_ID.equals(b.getGenre().getId()))
                .anyMatch(
                        b -> BOOK_ID_SECOND.equals(b.getId()) &&
                                BOOK_NAME.equals(b.getName()) &&
                                BOOK_DESCRIPTION.equals(b.getDescription()) &&
                                AUTHOR_ID.equals(b.getAuthor().getId()) &&
                                GENRE_ID.equals(b.getGenre().getId()));
    }

    private static void verifyGetSingleBook(MvcResult result) throws Exception{
        MockHttpServletResponse response = result.getResponse();
        String responseJson = response.getContentAsString();

        Book parsedBook = new ObjectMapper().readValue(responseJson, Book.class);
        assertAll(
                () -> assertThat(parsedBook.getId()).isEqualTo(BOOK_ID_FIRST),
                () -> assertThat(parsedBook.getName()).isEqualTo(BOOK_NAME),
                () -> assertThat(parsedBook.getDescription()).isEqualTo(BOOK_DESCRIPTION),
                () -> assertThat(parsedBook.getAuthor().getId()).isEqualTo(AUTHOR_ID),
                () -> assertThat(parsedBook.getGenre().getId()).isEqualTo(GENRE_ID)
        );
    }

    private static void verifyCreateBookResult(MvcResult result) throws Exception{
        MockHttpServletResponse response = result.getResponse();
        String responseJson = response.getContentAsString();

        Book parsedBook = new ObjectMapper().readValue(responseJson, Book.class);
        assertAll(
                () -> assertThat(parsedBook.getId()).isEqualTo(NEW_BOOK_ID),
                () -> assertThat(parsedBook.getName()).isEqualTo(BOOK_NAME),
                () -> assertThat(parsedBook.getDescription()).isEqualTo(BOOK_DESCRIPTION),
                () -> assertThat(parsedBook.getAuthor().getId()).isEqualTo(AUTHOR_ID),
                () -> assertThat(parsedBook.getGenre().getId()).isEqualTo(GENRE_ID)
        );
    }

    private static void verifyUpdateBookResult(MvcResult result) throws Exception{
        MockHttpServletResponse response = result.getResponse();
        String responseJson = response.getContentAsString();

        Book parsedBook = new ObjectMapper().readValue(responseJson, Book.class);
        assertAll(
                () -> assertThat(parsedBook.getId()).isEqualTo(BOOK_ID_FIRST),
                () -> assertThat(parsedBook.getName()).isEqualTo(BOOK_NAME),
                () -> assertThat(parsedBook.getDescription()).isEqualTo(BOOK_DESCRIPTION),
                () -> assertThat(parsedBook.getAuthor().getId()).isEqualTo(AUTHOR_ID),
                () -> assertThat(parsedBook.getGenre().getId()).isEqualTo(GENRE_ID)
        );
    }

    static String getBookDTOJson(Long bookId){
        return "{" +
                (bookId == null? "": "\"id\": " + bookId + ",") +
                "  \"authorId\": " + AUTHOR_ID + "," +
                "  \"description\": \"" + BOOK_DESCRIPTION + "\"," +
                "  \"genreId\": " + GENRE_ID + "," +
                "  \"name\": \"" + BOOK_NAME + "\"" +
                "}";
    }

    private void verifyUnauthorizedForWrongPass(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        // Проверим, что при неверном вводе пароля токен получить не удастся
        String body = "{\"login\":\"" + ADMIN_LOGIN + "\", \"password\":\"" + "wrong_pass" + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(body))
                .andExpect(status().isUnauthorized()).andReturn();
    }


    private String getAuthString(String login, String password, boolean isOk) throws Exception {
        String body = "{\"login\":\"" + login + "\", \"password\":\"" + password + "\"}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(body))
                .andExpect(isOk ? status().isOk(): status().isUnauthorized()).andReturn();

        String response = result.getResponse().getContentAsString();

        if(!isOk){
            assertThat(response).isBlank();
            return response;
        }
        return "Bearer " + response.replace(login, "").trim();
    }

    private String getAuthString(String login, String password) throws Exception {
        return getAuthString(login, password, true);
    }

    private void assertThrowsOnWrongToken(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        String token = getAuthString(ADMIN_LOGIN, ADMIN_PASS);

        assertThrows(SignatureVerificationException.class, () -> mockMvc.perform(requestBuilder
                .header(AUTHORIZATION, token.substring(0, token.length() - 3) + "zZz"))
                .andExpect(status().isForbidden()));
    }
}
