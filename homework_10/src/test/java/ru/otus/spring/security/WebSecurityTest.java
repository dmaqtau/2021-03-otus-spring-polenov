package ru.otus.spring.security;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.spring.config.TestSpringSecurityConfig.ACTIVE_USER_LOGIN;
import static ru.otus.spring.config.TestSpringSecurityConfig.ACTIVE_USER_PASS;
import static ru.otus.spring.config.TestSpringSecurityConfig.INACTIVE_USER_LOGIN;
import static ru.otus.spring.config.TestSpringSecurityConfig.INACTIVE_USER_PASS;

@Import({TestSpringSecurityConfig.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest({PageController.class, BookController.class})
class WebSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private static final String LOGIN_PAGE = "http://localhost/login";
    private static final String LOGIN_ERROR_PATH = "/login?error";

    private static final Long NEW_BOOK_ID = 10L;
    private static final Long BOOK_ID_FIRST = 2L;
    private static final Long BOOK_ID_SECOND = 3L;

    private static final Long AUTHOR_ID = 1L;
    private static final Long GENRE_ID = 1L;
    private static final String BOOK_NAME = "test_book_name";
    private static final String BOOK_DESCRIPTION = "test_book_description";
    private static final String LOCATION_PARAM = "Location";

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

    @DisplayName("Должно перенаправить запросы GET на страницу аутентификации")
    @ParameterizedTest
    @ValueSource(strings = {"/about", "/api/book", "/api/book/1"})
    void testRedirectGetToLoginPage(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(LOCATION_PARAM, LOGIN_PAGE));
    }

    @DisplayName("Должно перенаправить запрос PUT на страницу аутентификации")
    @Test
    void testRedirectPutToLoginPage() throws Exception {
        mockMvc.perform(put("/api/book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(LOCATION_PARAM, LOGIN_PAGE));
    }

    @DisplayName("Должно перенаправить запрос POST на страницу аутентификации")
    @Test
    void testRedirectPostToLoginPage() throws Exception {
        mockMvc.perform(post("/api/book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(LOCATION_PARAM, LOGIN_PAGE));
    }

    @DisplayName("Должно перенаправить запрос DELETE на страницу аутентификации")
    @Test
    void testRedirectDeleteToLoginPage() throws Exception {
        mockMvc.perform(delete("/api/book/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(LOCATION_PARAM, LOGIN_PAGE));
    }

    @WithUserDetails(ACTIVE_USER_LOGIN)
    @DisplayName("Должны успешно открыть страницу about для ранее авторизованного пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"/about", "/api/book/1"})
    void testSuccessAboutForAuthorizedUser(String path) throws Exception {
        mockMvc.perform(get(path)).andExpect(status().isOk());
    }

    @DisplayName("Должны успешно авторизоваться и открыть страницу about")
    @Test
    void testSuccessAuthorizeAndOpenAboutPage() throws Exception {
        MockHttpSession session = getSession(ACTIVE_USER_LOGIN, ACTIVE_USER_PASS, true);
        mockMvc.perform(get("/about").session(session)).andExpect(status().isOk());
    }

    @WithUserDetails(ACTIVE_USER_LOGIN)
    @DisplayName("Должны успешно получить список книг для ранее авторизованного пользователя")
    @Test
    void testSuccessGetAllBooksForAuthorizedUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/book"))
                .andExpect(status().isOk())
                .andReturn();
        verifyGetBookList(result);
    }

    @DisplayName("Должны успешно авторизоваться и получить список книг")
    @Test
    void testSuccessAuthAndGetAllBooksForUser() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyRejectActiveuserWrongPass( get("/api/book"));
        
        MockHttpSession session = getSession(ACTIVE_USER_LOGIN, ACTIVE_USER_PASS, true);
        MvcResult result = mockMvc.perform(get("/api/book").session(session))
                .andExpect(status().isOk())
                .andReturn();
        verifyGetBookList(result);
    }

    @WithUserDetails(ACTIVE_USER_LOGIN)
    @DisplayName("Должны успешно получить книгу для ранее авторизованного пользователя")
    @Test
    void testSuccessGetBookForAuthorizedUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/book/" + BOOK_ID_FIRST))
                .andExpect(status().isOk())
                .andReturn();

        verifyGetSingleBook(result);
    }

    @DisplayName("Должны успешно авторизоваться и получить книгу")
    @Test
    void testSuccessAuthAndGetBookForUser() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyRejectActiveuserWrongPass( get("/api/book/" + BOOK_ID_FIRST));
        
        MockHttpSession session = getSession(ACTIVE_USER_LOGIN, ACTIVE_USER_PASS, true);
        MvcResult result = mockMvc.perform(get("/api/book/" + BOOK_ID_FIRST).session(session))
                .andExpect(status().isOk())
                .andReturn();
        verifyGetSingleBook(result);
    }

    @WithUserDetails(ACTIVE_USER_LOGIN)
    @DisplayName("Должны успешно выполнить запрос PUT для ранее авторизованного пользователя")
    @Test
    void testSuccessPutForAuthorizedUser() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(BOOK_ID_FIRST)))
                .andExpect(status().isOk()).andReturn();

        verifyUpdateBookResult(result);
    }

    @DisplayName("Должны успешно авторизоваться и обновить книгу")
    @Test
    void testSuccessAuthAndUpdateBook() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyRejectActiveuserWrongPass( put("/api/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(BOOK_ID_FIRST)));
        
        MockHttpSession session = getSession(ACTIVE_USER_LOGIN, ACTIVE_USER_PASS, true);
        MvcResult result = mockMvc.perform(put("/api/book")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(BOOK_ID_FIRST)))
                .andExpect(status().isOk())
                .andReturn();

        verifyUpdateBookResult(result);
    }

    @WithUserDetails(ACTIVE_USER_LOGIN)
    @DisplayName("Должны успешно выполнить запрос POST для ранее авторизованного пользователя")
    @Test
    void testSuccessPostForAuthorizedUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(null)))
                .andExpect(status().isOk()).andReturn();

        verifyCreateBookResult(result);
    }

    @DisplayName("Должны успешно авторизоваться и создать книгу")
    @Test
    void testSuccessAuthAndCreateBook() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyRejectActiveuserWrongPass( post("/api/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(null)));

        MockHttpSession session = getSession(ACTIVE_USER_LOGIN, ACTIVE_USER_PASS, true);
        MvcResult result = mockMvc.perform(post("/api/book")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(null)))
                .andExpect(status().isOk()).andReturn();

        verifyCreateBookResult(result);
    }

    @WithUserDetails(ACTIVE_USER_LOGIN)
    @DisplayName("Должны успешно выполнить запрос DELETE для ранее авторизованного пользователя")
    @Test
    void testSuccessDeleteForAuthorizedUser() throws Exception {
        mockMvc.perform(delete("/api/book/1"))
                .andExpect(status().isOk());
    }

    @DisplayName("Должны успешно авторизоваться и создать книгу")
    @Test
    void testSuccessAuthAndDeleteBook() throws Exception {
        // Вначале проверим попытку выполнить запрос с неверным паролем
        verifyRejectActiveuserWrongPass( delete("/api/book/1"));

        MockHttpSession session = getSession(ACTIVE_USER_LOGIN, ACTIVE_USER_PASS, true);

        mockMvc.perform(delete("/api/book/1").session(session))
                .andExpect(status().isOk());
    }

    @DisplayName("Должны отклонить переход на страницу about для неактивного пользователя")
    @Test
    void testRejectAboutPageForInactiveUser() throws Exception {
        MockHttpSession session = getSession(INACTIVE_USER_LOGIN, INACTIVE_USER_PASS, false);

        mockMvc.perform(get("/about")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(LOCATION_PARAM, LOGIN_PAGE));
    }

    @DisplayName("Должны отклонить запрос DELETE для неактивного пользователя")
    @Test
    void testRejectDeleteForInactiveUser() throws Exception {
        MockHttpSession session = getSession(INACTIVE_USER_LOGIN, INACTIVE_USER_PASS, false);

        mockMvc.perform(delete("/api/book/1")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(LOCATION_PARAM, LOGIN_PAGE));
    }

    @DisplayName("Должны отклонить запрос PUT для неактивного пользователя")
    @Test
    void testRejectPutForInactiveUser() throws Exception {
        MockHttpSession session = getSession(INACTIVE_USER_LOGIN, INACTIVE_USER_PASS, false);

        mockMvc.perform(put("/api/book")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(BOOK_ID_FIRST)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(LOCATION_PARAM, LOGIN_PAGE));
    }

    @DisplayName("Должны отклонить запрос POST для неактивного пользователя")
    @Test
    void testRejectPostForInactiveUser() throws Exception {
        MockHttpSession session = getSession(INACTIVE_USER_LOGIN, INACTIVE_USER_PASS, false);

        mockMvc.perform(post("/api/book")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(getBookDTOJson(null)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(LOCATION_PARAM, LOGIN_PAGE));
    }

    @WithUserDetails(ACTIVE_USER_LOGIN)
    @DisplayName("Должны получать ошибку 404 в случае несуществующей страницы")
    @Test
    void testNotFound() throws Exception {
        mockMvc.perform(get("/not/existing/path"))
                .andExpect(status().isNotFound());
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

    private static String getBookDTOJson(Long bookId){
        return "{" +
                (bookId == null? "": "\"id\": " + bookId + ",") +
                "  \"authorId\": " + AUTHOR_ID + "," +
                "  \"description\": \"" + BOOK_DESCRIPTION + "\"," +
                "  \"genreId\": " + GENRE_ID + "," +
                "  \"name\": \"" + BOOK_NAME + "\"" +
                "}";
    }

    private void verifyRejectActiveuserWrongPass(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        // Проверим, что при неверном вводе пароля доступ запрещён
        MvcResult mvcResult = mockMvc.perform(formLogin().user(ACTIVE_USER_LOGIN).password("wrong_pass"))
                .andExpect(header().string(LOCATION_PARAM, LOGIN_ERROR_PATH))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated()).andReturn();
        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        assertThat(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION")
                .getClass())
                .isEqualTo(BadCredentialsException.class);   // Ошибка авторизации: неверные данные авторизации

        mockMvc.perform(requestBuilder.session(session)).andExpect(status().is3xxRedirection());
    }

    private MockHttpSession getSession(String username, String password, boolean shouldAuthenticate) throws Exception {
        ResultActions actions = mockMvc.perform(formLogin().user(username).password(password))
                .andExpect(shouldAuthenticate? SecurityMockMvcResultMatchers.authenticated().withUsername(username): SecurityMockMvcResultMatchers.unauthenticated());

        if(!shouldAuthenticate){
            actions = actions.andExpect(header().string(LOCATION_PARAM, LOGIN_ERROR_PATH));
        }

        MvcResult mvcResult = actions.andReturn();

        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        if(!shouldAuthenticate) {
            assertThat(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION")
                    .getClass())
                    .isEqualTo(DisabledException.class);   // Ошибка авторизации: юзер не активен
        }
        return session;
    }
}
