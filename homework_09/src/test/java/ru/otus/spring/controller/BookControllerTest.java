package ru.otus.spring.controller;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import ru.otus.spring.Main;
import ru.otus.spring.domain.Book;
import ru.otus.spring.dto.BookDTO;
import ru.otus.spring.dto.ErrorNotification;
import ru.otus.spring.service.BookService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Интеграционные тесты контроллера BookController")
@Sql({ "classpath:schema.sql", "classpath:data.sql" })
@SpringBootTest(classes = Main.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    @LocalServerPort
    private int port;

    @SpyBean
    private BookService bookService;
    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private static final String NEW_BOOK_NAME = "new_book_name";
    private static final Long NEW_AUTHOR_ID = 2L;
    private static final Long NEW_GENRE_ID = 2L;

    private static final Long EXISTING_AUTHOR_ID = 3L;
    private static final Long NOT_EXISTING_AUTHOR_ID = 3000L;

    private static final String EXISTING_AUTHOR_NAME= "Зиро";
    private static final String EXISTING_AUTHOR_SURNAME= "Саб";
    private static final String EXISTING_AUTHOR_PATRONYMIC = "Тест";

    private static final String NEW_AUTHOR_NAME= "Такеда";
    private static final String NEW_AUTHOR_SURNAME = "Ю";
    private static final String NEW_AUTHOR_PATRONYMIC = "Ноунеймович";

    private static final Long EXISTING_GENRE_ID = 1L;
    private static final String EXISTING_GENRE_NAME = "Ненаучная фантастика";
    private static final String NEW_GENRE_NAME = "Научная фантастика";

    private static final Long NOT_EXISTING_GENRE_ID = 3001L;
    private static final Long EXISTING_BOOK_ID = 4L;
    private static final Long NOT_EXISTING_BOOK_ID = 4000L;
    private static final Long INVALID_BOOK_ID = -5L;

    private static final int EXPECTED_BOOKS_COUNT = 6;

    private static final Integer EXPECTED_COMMENTS_SIZE = 4;
    private static final String NEW_DESCRIPTION = "new_description";


    @Test
    @DisplayName("Должны успешно создать новую книгу")
    void shouldCreateNewBook() {
        BookDTO dto = new BookDTO(null, NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);

        ResponseEntity<Book> response = this.restTemplate.postForEntity(String.format("http://localhost:%d/api/book", port), dto, Book.class);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().getAuthor().getId()).isEqualTo(dto.getAuthorId()),
                () -> assertThat(response.getBody().getAuthor().getName()).isNotNull(),
                () -> assertThat(response.getBody().getGenre().getId()).isEqualTo(dto.getGenreId()),
                () -> assertThat(response.getBody().getGenre().getName()).isNotNull(),
                () -> assertThat(response.getBody().getName()).isEqualTo(dto.getName()),
                () -> assertThat(response.getBody().getDescription()).isEqualTo(dto.getDescription())
        );
    }

    @Test
    @DisplayName("Должны получить ошибку 404 создания книги при несуществующем авторе")
    void shouldGet404OnCreateBookWithNotExistingAuthor() {
        BookDTO dto = new BookDTO(null, NEW_BOOK_NAME, NEW_DESCRIPTION, NOT_EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);
        assertResponseError(createBookErr(dto), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Должны получить ошибку 404 создания книги при несуществующем жанре")
    void shouldGet404OnCreateBookWithNotExistingGenre() {
        BookDTO dto = new BookDTO(null, NEW_BOOK_NAME, NEW_DESCRIPTION, NOT_EXISTING_GENRE_ID, EXISTING_AUTHOR_ID);
        assertResponseError(createBookErr(dto), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Должны получить ошибку 400 создания книги при неверном ID автора")
    void shouldGet400OnCreateBookWithNotExistingAuthor() {
        BookDTO dto = new BookDTO(null, NEW_BOOK_NAME, NEW_DESCRIPTION, INVALID_BOOK_ID, EXISTING_GENRE_ID);
        assertResponseError(createBookErr(dto), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Должны получить ошибку 400 создания книги при неверном ID жанра")
    void shouldGet400OnCreateBookWithNotExistingGenre() {
        BookDTO dto = new BookDTO(null, NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, INVALID_BOOK_ID);
        assertResponseError(createBookErr(dto), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Должны успешно получить перечень книг")
    void shouldGetAllBooks(){
        ResponseEntity<List> response = getAllBooks();
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasSize(EXPECTED_BOOKS_COUNT)
        );
    }

    @Test
    @DisplayName("Должны успешно получить одну книгу по идентификатору")
    void shouldGetSingleBook(){
        ResponseEntity<Book> response = getSingleBook();
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().getName()).isNotNull(),
                () -> assertThat(response.getBody().getDescription()).isNotNull(),
                () -> assertThat(response.getBody().getAuthor()).isNotNull(),
                () -> assertThat(response.getBody().getGenre()).isNotNull(),
                () -> assertThat(response.getBody().getAuthor().getId()).isEqualTo(EXISTING_AUTHOR_ID),
                () -> assertThat(response.getBody().getAuthor().getName()).isEqualTo(EXISTING_AUTHOR_NAME),
                () -> assertThat(response.getBody().getAuthor().getSurname()).isEqualTo(EXISTING_AUTHOR_SURNAME),
                () -> assertThat(response.getBody().getAuthor().getPatronymic()).isEqualTo(EXISTING_AUTHOR_PATRONYMIC),
                () -> assertThat(response.getBody().getGenre().getId()).isEqualTo(EXISTING_GENRE_ID),
                () -> assertThat(response.getBody().getGenre().getName()).isEqualTo(EXISTING_GENRE_NAME)
        );
    }

    @Test
    @DisplayName("Должны получить ошибку 404 при попытке получения книги по несуществующему ID")
    void shouldGet404OnGetBookByNotExistingID() {
        assertResponseError(getSingleBookErr(NOT_EXISTING_BOOK_ID), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Должны получить ошибку 400 при попытке получения книги по некорректному ID")
    void shouldGet400OnGetBookByInvalidID() {
        assertResponseError(getSingleBookErr(INVALID_BOOK_ID), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Должны успешно обновить книгу")
    void shouldUpdateBook(){
        BookDTO dto = new BookDTO(EXISTING_BOOK_ID, NEW_BOOK_NAME, NEW_DESCRIPTION, NEW_AUTHOR_ID, NEW_GENRE_ID);
        Book updatedBook = updateBook(dto).getBody();

        assertAll(
                () -> assertThat(updatedBook).isNotNull(),
                () -> assertThat(updatedBook.getName()).isEqualTo(NEW_BOOK_NAME),
                () -> assertThat(updatedBook.getDescription()).isEqualTo(NEW_DESCRIPTION),
                () -> assertNewAuthorAndGenre(updatedBook)
        );

        dto = new BookDTO(EXISTING_BOOK_ID, NEW_BOOK_NAME, null, 0L, 0L);
        ResponseEntity<Book> response = updateBook(dto);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().getDescription()).isNull(),
                () -> assertNewAuthorAndGenre(response.getBody())
        );
    }

    @Test
    @DisplayName("Должны получить ошибку при попытке обновления книги с несуществующим идентификатором")
    void shouldGet404OnUpdateBookByNotExistingID() {
        BookDTO dto = new BookDTO(NOT_EXISTING_BOOK_ID, NEW_BOOK_NAME, NEW_DESCRIPTION, NEW_AUTHOR_ID, NEW_GENRE_ID);

        ResponseEntity<ErrorNotification> response = updateBookErr(dto);
        assertResponseError(response, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Должны получить ошибку при попытке обновления книги с некорректным идентификатором")
    void shouldGet400OnUpdateBookByInvalidID() {
        BookDTO dto = new BookDTO(INVALID_BOOK_ID, NEW_BOOK_NAME, NEW_DESCRIPTION, NEW_AUTHOR_ID, NEW_GENRE_ID);

        ResponseEntity<ErrorNotification> response = updateBookErr(dto);
        assertResponseError(response, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Должны удалить книгу по переданному идентификатору")
    void shouldDeleteBookById(){
        int countBefore = getAllBooks().getBody().size();

        ResponseEntity<Book> response = deleteBookById(EXISTING_BOOK_ID);
        assertAll(
                () -> assertThat(HttpStatus.OK.equals(response.getStatusCode())),
                () -> assertThat(getAllBooks().getBody().size()).isEqualTo(countBefore - 1)
        );
    }

    @Test
    @DisplayName("Должны получить ошибку 404 при попытке удаления книги с несуществующим идентификатором")
    void shouldGet404OnDeleteBookByNotExistingID() {
        ResponseEntity<ErrorNotification> response = deleteBookByIdErr(NOT_EXISTING_BOOK_ID);
        assertResponseError(response, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Должны получить ошибку 400 при попытке удаления книги с некорректным идентификатором")
    void shouldGet400OnDeleteBookByInvalidID() {
        ResponseEntity<ErrorNotification> response = deleteBookByIdErr(INVALID_BOOK_ID);
        assertResponseError(response, HttpStatus.BAD_REQUEST);
    }

    private static void assertNewAuthorAndGenre(Book book){
        assertAll(
                () -> assertThat(book.getAuthor()).isNotNull(),
                () -> assertThat(book.getGenre()).isNotNull(),
                () -> assertThat(book.getAuthor().getName()).isEqualTo(NEW_AUTHOR_NAME),
                () -> assertThat(book.getAuthor().getSurname()).isEqualTo(NEW_AUTHOR_SURNAME),
                () -> assertThat(book.getAuthor().getPatronymic()).isEqualTo(NEW_AUTHOR_PATRONYMIC),
                () -> assertThat(book.getGenre().getName()).isEqualTo(NEW_GENRE_NAME)
        );
    }

    private ResponseEntity<ErrorNotification> deleteBookByIdErr(Long bookId){
        return restTemplate.exchange(String.format("http://localhost:%d/api/book/%d", port, bookId), HttpMethod.DELETE, null, ErrorNotification.class);
    }

    private ResponseEntity<Book> deleteBookById(Long bookId){
        return restTemplate.exchange(String.format("http://localhost:%d/api/book/%d", port, bookId), HttpMethod.DELETE, null, Book.class);
    }

    private ResponseEntity<Book> updateBook(BookDTO bookDTO){
        HttpEntity<BookDTO> entity = new HttpEntity<>(bookDTO);
        return restTemplate.exchange(String.format("http://localhost:%d/api/book", port), HttpMethod.PUT, entity, Book.class);
    }

    private ResponseEntity<ErrorNotification> updateBookErr(BookDTO bookDTO){
        HttpEntity<BookDTO> entity = new HttpEntity<>(bookDTO);
        return restTemplate.exchange(String.format("http://localhost:%d/api/book", port), HttpMethod.PUT, entity, ErrorNotification.class);
    }

    private ResponseEntity<Book> getSingleBook(){
        return this.restTemplate.getForEntity(String.format("http://localhost:%d/api/book/%d", port, EXISTING_BOOK_ID), Book.class);
    }

    private ResponseEntity<ErrorNotification> getSingleBookErr(Long bookId){
        return this.restTemplate.getForEntity(String.format("http://localhost:%d/api/book/%d", port, bookId), ErrorNotification.class);
    }

    private ResponseEntity<ErrorNotification> createBookErr(BookDTO dto){
        return this.restTemplate.postForEntity(String.format("http://localhost:%d/api/book", port), dto, ErrorNotification.class);
    }

    private ResponseEntity<List> getAllBooks(){
        return  this.restTemplate.getForEntity(String.format("http://localhost:%d/api/books", port), List.class);
    }

    private static void assertResponseError(ResponseEntity<ErrorNotification> response, HttpStatus httpStatus){
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(httpStatus),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().getStatus()).isEqualTo(httpStatus.getReasonPhrase())
        );
    }
}
