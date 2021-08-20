package ru.otus.spring.shell;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.service.BookService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.otus.spring.shell.BookShellCommands.getBookInfo;

@DisplayName("Тестирование сервиса BookShellCommands")
@SpringBootTest
class BookShellCommandsTest {
    @MockBean
    private BookService bookService;
    @Autowired
    BookShellCommands bookShellCommands;

    private static final String EXISTING_BOOK_NAME = "existing_book_name";
    private static final String NEW_BOOK_NAME = "new_book_name";
    private static final Long EXISTING_AUTHOR_ID = 2L;
    private static final String EXISTING_DESCRIPTION = "existing_book_description";
    private static final String NEW_DESCRIPTION = "new_book_description";
    private static final String EXISTING_AUTHOR_NAME = "existing_author_name";
    private static final String EXISTING_GENRE_NAME = "existing_genre_name";

    private static final Long WRONG_AUTHOR_ID = -1L;
    private static final Long WRONG_GENRE_ID = -1L;
    private static final Long EXISTING_GENRE_ID = 3L;
    private static final Long EXISTING_BOOK_ID = 4L;
    private static final Long NEW_BOOK_ID = 10L;
    private static final Long NOT_EXISTING_BOOK_ID = 5L;

    @Test
    @DisplayName("Должны выполнить вызов команды сервиса на получение всех книг")
    void shouldCallGetAllBooks(){
        given(bookService.getAll()).willReturn(List.of(Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName(EXISTING_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID)
                        .authorName(EXISTING_AUTHOR_NAME)
                        .build())
                .description(EXISTING_DESCRIPTION)
                .genre(Genre.builder()
                        .id(EXISTING_GENRE_ID)
                        .genreName(EXISTING_GENRE_NAME)
                        .build())
                .build()));
        String result = bookShellCommands.listBooks();

        assertAll(
                () -> verify(bookService).getAll(),
                () -> assertThat(result)
                        .contains
                                (List.of(
                                        String.valueOf(EXISTING_BOOK_ID),
                                        EXISTING_BOOK_NAME,
                                        EXISTING_AUTHOR_NAME,
                                        EXISTING_DESCRIPTION,
                                        EXISTING_GENRE_NAME))
        );
    }

    @Test
    @DisplayName("Должны корректно удалить одну книгу по идентификатору")
    void shouldDeleteBookById(){
        given(bookService.deleteByID(anyLong())).willReturn(1);
        String result = bookShellCommands.deleteById(EXISTING_BOOK_ID);
        verify(bookService).deleteByID(EXISTING_BOOK_ID);
        assertThat(result).contains(String.format(BookShellCommands.DELETED_BOOK_TEMPLATE, EXISTING_BOOK_ID));

        given(bookService.deleteByID(anyLong())).willReturn(0);
        result = bookShellCommands.deleteById(NOT_EXISTING_BOOK_ID);
        verify(bookService).deleteByID(NOT_EXISTING_BOOK_ID);
        assertThat(result).contains(String.format(BookShellCommands.NOT_DELETED_BOOKS_TEMPLATE, NOT_EXISTING_BOOK_ID));
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке при удалении по идентификатору")
    void shouldShowErrMsgOnDeleteBookByIdException(){
        given(bookService.deleteByID(anyLong())).willThrow(RuntimeException.class);
        String result = bookShellCommands.deleteById(NOT_EXISTING_BOOK_ID);
        verify(bookService).deleteByID(NOT_EXISTING_BOOK_ID);
        assertThat(result).contains(BookShellCommands.FAILED_TO_DELETE_BOOKS_TEMPLATE);
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке поиска всех книг")
    void shouldShowErrMsgOnListAllBooksException(){
        given(bookService.getAll()).willThrow(RuntimeException.class);
        String result = bookShellCommands.listBooks();
        verify(bookService).getAll();
        assertThat(result).contains(BookShellCommands.FAILED_TO_GET_BOOKS_LIST_TEMPLATE);
    }

    @Test
    @DisplayName("Должны получить одну книгу по идентификатору")
    void shouldGetBookById(){
        Book existingBook = Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName(EXISTING_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID)
                        .authorName(EXISTING_AUTHOR_NAME)
                        .build())
                .description(EXISTING_DESCRIPTION)
                .genre(Genre.builder()
                        .id(EXISTING_GENRE_ID)
                        .genreName(EXISTING_GENRE_NAME)
                        .build())
                .build();

        given(bookService.getByID(anyLong())).willReturn(existingBook);
        String result = bookShellCommands.getById(EXISTING_BOOK_ID);
        verify(bookService).getByID(EXISTING_BOOK_ID);
        assertThat(result).contains(String.format(BookShellCommands.FOUND_BOOK_BY_ID_TEMPLATE, EXISTING_BOOK_ID,  getBookInfo(existingBook)));

        given(bookService.getByID(anyLong())).willReturn(null);
        result = bookShellCommands.getById(NOT_EXISTING_BOOK_ID);
        verify(bookService).getByID(NOT_EXISTING_BOOK_ID);
        assertThat(result).contains(String.format(BookShellCommands.NOT_FOUND_BOOK_BY_ID_TEMPLATE, NOT_EXISTING_BOOK_ID));
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке при получении книги по идентификатору")
    void shouldShowErrMsgOnGetBookByIdException(){
        given(bookService.getByID(anyLong())).willThrow(RuntimeException.class);
        String result = bookShellCommands.getById(NOT_EXISTING_BOOK_ID);
        verify(bookService).getByID(NOT_EXISTING_BOOK_ID);
        assertThat(result).contains(BookShellCommands.FAILED_TO_GET_BY_ID_TEMPLATE);
    }

    @Test
    @DisplayName("Должны успешно добавить книгу")
    void shouldCreateBook(){
        when(bookService.create(anyString(), anyString(), anyLong(), anyLong()))
                .thenAnswer((Answer<Book>) invocation -> {
                    Object[] args = invocation.getArguments();
                    String bookName = (String)args[0];
                    String bookDescr = (String)args[1];
                    long authorId = (long) args[2];
                    long genreId = (long) args[3];

                    return Book.builder()
                            .id(NEW_BOOK_ID)
                            .bookName(bookName)
                            .description(bookDescr)
                            .author(Author.builder()
                                    .id(authorId)
                                    .authorName(EXISTING_AUTHOR_NAME)
                                    .build()
                            ).genre(Genre.builder()
                                    .id(genreId)
                                    .genreName(EXISTING_GENRE_NAME)
                                    .build())
                            .build();
                });
        String result = bookShellCommands.createBook(NEW_BOOK_NAME, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID, NEW_DESCRIPTION);
        verify(bookService).create(NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);
        assertThat(result).contains (
                NEW_BOOK_NAME,
                NEW_DESCRIPTION,
                String.valueOf(NEW_BOOK_ID),
                EXISTING_AUTHOR_NAME,
                EXISTING_GENRE_NAME);
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке при получении книги по идентификатору")
    void shouldShowErrMsgOnCreateBookException(){
        given(bookService.create(anyString(), anyString(), anyLong(), anyLong())).willThrow(RuntimeException.class);
        String result = bookShellCommands.createBook(NEW_BOOK_NAME, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID, NEW_DESCRIPTION);
        verify(bookService).create(NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);
        assertThat(result).contains(BookShellCommands.FAILED_TO_CREATE_BOOK_TEMPLATE);
    }

    @Test
    @DisplayName("Должны успешно обновить книгу")
    void shouldUpdateBook(){
        when(bookService.update(anyLong(), anyString(), anyString(), anyLong(), anyLong()))
                .thenAnswer((Answer<Book>) invocation -> {
                    Object[] args = invocation.getArguments();
                    long bookId = (long) args[0];
                    String bookName = (String)args[1];
                    String bookDescr = (String)args[2];
                    long authorId = (long) args[3];
                    long genreId = (long) args[4];

                    return Book.builder()
                            .id(bookId)
                            .bookName(bookName)
                            .description(bookDescr)
                            .author(Author.builder()
                                            .id(authorId)
                                            .authorName(EXISTING_AUTHOR_NAME)
                                            .build()
                            ).genre(Genre.builder()
                                            .id(genreId)
                                            .genreName(EXISTING_GENRE_NAME)
                                            .build())
                            .build();
                });

        String result = bookShellCommands.updateBook(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID, EXISTING_DESCRIPTION);
        verify(bookService).update(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, EXISTING_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);
        assertThat(result).contains (
                EXISTING_BOOK_NAME,
                EXISTING_DESCRIPTION,
                String.valueOf(EXISTING_BOOK_ID),
                EXISTING_AUTHOR_NAME,
                EXISTING_GENRE_NAME);
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке при получении книги по идентификатору")
    void shouldShowErrMsgOnUpdateBookException(){
        given(bookService.update(anyLong(), anyString(), anyString(), anyLong(), anyLong())).willThrow(RuntimeException.class);
        String result = bookShellCommands.updateBook(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID, EXISTING_DESCRIPTION);
        verify(bookService).update(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, EXISTING_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);
        assertThat(result).contains(BookShellCommands.FAILED_TO_UPDATE_BOOK_TEMPLATE);
    }
}
