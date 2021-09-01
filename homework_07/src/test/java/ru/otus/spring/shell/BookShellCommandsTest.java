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
import ru.otus.spring.domain.BookComment;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.exception.CommentValidationException;
import ru.otus.spring.service.BookService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.otus.spring.shell.BookShellCommands.COMMENT_BY_BOOK_ID_DELETED_MSG;
import static ru.otus.spring.shell.BookShellCommands.COMMENT_DELETED_MSG;
import static ru.otus.spring.shell.BookShellCommands.FAILED_TO_DELETE_COMMENT;
import static ru.otus.spring.shell.BookShellCommands.getBookInfo;

@DisplayName("Тестирование сервиса BookShellCommands")
@SpringBootTest
class BookShellCommandsTest {
    @MockBean
    private BookService bookService;
    @Autowired
    private BookShellCommands bookShellCommands;

    private static final String EXISTING_BOOK_NAME = "existing_book_name";
    private static final String NEW_BOOK_NAME = "new_book_name";
    private static final Long EXISTING_AUTHOR_ID = 2L;
    private static final String EXISTING_DESCRIPTION = "existing_book_description";
    private static final String NEW_DESCRIPTION = "new_book_description";
    private static final String EXISTING_AUTHOR_NAME = "existing_author_name";
    private static final String EXISTING_AUTHOR_SURNAME = "existing_author_surname";
    private static final String EXISTING_GENRE_NAME = "existing_genre_name";

    private static final Long WRONG_AUTHOR_ID = -1L;
    private static final Long WRONG_GENRE_ID = -1L;
    private static final Long EXISTING_GENRE_ID = 3L;
    private static final Long EXISTING_BOOK_ID = 4L;
    private static final Long NEW_BOOK_ID = 10L;
    private static final Long NOT_EXISTING_BOOK_ID = 5L;

    private static final Long EXISTING_COMMENT_ID = 40L;
    private static final Long NOT_EXISTING_COMMENT_ID = 41L;
    @Test
    @DisplayName("Должны выполнить вызов команды сервиса на получение всех книг")
    void shouldCallGetAllBooks(){
        Author author = new Author(EXISTING_AUTHOR_ID, EXISTING_AUTHOR_SURNAME, EXISTING_AUTHOR_NAME, null);
        Genre genre = new Genre(EXISTING_GENRE_ID, EXISTING_GENRE_NAME);

        given(bookService.getAll()).willReturn(
                List.of(new Book(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, author, genre, EXISTING_DESCRIPTION))
        );
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
        String result = bookShellCommands.deleteById(EXISTING_BOOK_ID);
        verify(bookService).deleteByID(EXISTING_BOOK_ID);
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке при удалении по идентификатору")
    void shouldShowErrMsgOnDeleteBookByIdException(){
         doThrow(RuntimeException.class).when(bookService).deleteByID(anyLong());
        String result = bookShellCommands.deleteById(NOT_EXISTING_BOOK_ID);
        verify(bookService).deleteByID(NOT_EXISTING_BOOK_ID);
        assertThat(result).contains(BookShellCommands.FAILED_TO_DELETE_BOOKS);
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке поиска всех книг")
    void shouldShowErrMsgOnListAllBooksException(){
        given(bookService.getAll()).willThrow(RuntimeException.class);
        String result = bookShellCommands.listBooks();
        verify(bookService).getAll();
        assertThat(result).contains(BookShellCommands.FAILED_TO_GET_BOOKS_LIST);
    }

    @Test
    @DisplayName("Должны получить одну книгу по идентификатору")
    void shouldGetBookById(){
        Author author = new Author(EXISTING_AUTHOR_ID, EXISTING_AUTHOR_SURNAME, EXISTING_AUTHOR_NAME, null);
        Genre genre = new Genre(EXISTING_GENRE_ID, EXISTING_GENRE_NAME);
        Book existingBook = new Book(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, author, genre, EXISTING_DESCRIPTION);

        given(bookService.findByID(anyLong())).willReturn(existingBook);
        String result = bookShellCommands.getById(EXISTING_BOOK_ID);
        verify(bookService).findByID(EXISTING_BOOK_ID);
        assertThat(result).contains(String.format(BookShellCommands.FOUND_BOOK_BY_ID, EXISTING_BOOK_ID,  getBookInfo(existingBook)));

        given(bookService.findByID(anyLong())).willReturn(null);
        result = bookShellCommands.getById(NOT_EXISTING_BOOK_ID);
        verify(bookService).findByID(NOT_EXISTING_BOOK_ID);
        assertThat(result).contains(String.format(BookShellCommands.NOT_FOUND_BOOK_BY_ID, NOT_EXISTING_BOOK_ID));
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке при получении книги по идентификатору")
    void shouldShowErrMsgOnGetBookByIdException(){
        given(bookService.findByID(anyLong())).willThrow(RuntimeException.class);
        String result = bookShellCommands.getById(NOT_EXISTING_BOOK_ID);
        verify(bookService).findByID(NOT_EXISTING_BOOK_ID);
        assertThat(result).contains(BookShellCommands.FAILED_TO_GET_BY_ID);
    }

    @Test
    @DisplayName("Должны отобразить комментарии к книге")
    void shouldGetBookComments(){
        Book existingBook = new Book(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, null, null, EXISTING_DESCRIPTION);
        BookComment firstComment = new BookComment(1, existingBook, "testLogin1", "");
        BookComment secondComment = new BookComment(2, existingBook, "testLogin2", "");

        given(bookService.findComments(anyLong())).willReturn(List.of(firstComment, secondComment));
        String result = bookShellCommands.getComments(EXISTING_BOOK_ID);

        assertThat(result)
                .contains(String.format(BookShellCommands.FOUND_COMMENTS_FOR_BOOK, EXISTING_BOOK_ID, ""))
                .contains("testLogin")
                .contains("testLogin2");
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

                    Author author = new Author(authorId, EXISTING_AUTHOR_SURNAME, EXISTING_AUTHOR_NAME, null);
                    Genre genre = new Genre(genreId, EXISTING_GENRE_NAME);
                    return new Book(NEW_BOOK_ID, bookName, author, genre, bookDescr);
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
        assertThat(result).contains(BookShellCommands.FAILED_TO_CREATE_BOOK);
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

                    Author author = new Author(authorId, EXISTING_AUTHOR_SURNAME, EXISTING_AUTHOR_NAME, null);
                    Genre genre = new Genre(genreId, EXISTING_GENRE_NAME);
                    return new Book(bookId, bookName, author, genre, bookDescr);
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
    @DisplayName("Должны успешно удалить комментарии по идентификатору книги")
    void shouldDeleteCommentsByBookId(){
        String result = bookShellCommands.deleteComment(0, EXISTING_BOOK_ID);
        verify(bookService).deleteCommentByBookId(EXISTING_BOOK_ID);
        assertThat(result).contains(String.format(COMMENT_BY_BOOK_ID_DELETED_MSG, EXISTING_BOOK_ID));
    }

    @Test
    @DisplayName("Должны успешно удалить комментарии по идентификатору комментария")
    void shouldDeleteCommentsByCommentId(){
        String result = bookShellCommands.deleteComment(EXISTING_COMMENT_ID, 0);
        verify(bookService).deleteCommentById(EXISTING_COMMENT_ID);
        assertThat(result).contains(COMMENT_DELETED_MSG);
    }

    @Test
    @DisplayName("Должны выдать сообщение о неудаче при отсутствующем идентификаторе книги")
    void shouldDisplayErrOnDeletingCommentForNotExistingBookId(){
        doThrow(CommentValidationException.class).when(bookService).deleteCommentByBookId(NOT_EXISTING_BOOK_ID);
        String result = bookShellCommands.deleteComment(0, NOT_EXISTING_BOOK_ID);

        verify(bookService).deleteCommentByBookId(NOT_EXISTING_BOOK_ID);
        assertThat(result).contains(FAILED_TO_DELETE_COMMENT);
    }

    @Test
    @DisplayName("Должны выдать сообщение о неудаче при отсутствующем идентификаторе комментария")
    void shouldDisplayErrOnDeletingCommentForNotExistingId(){
        doThrow(CommentValidationException.class).when(bookService).deleteCommentById(NOT_EXISTING_COMMENT_ID);
        String result = bookShellCommands.deleteComment(NOT_EXISTING_COMMENT_ID, 0);

        verify(bookService).deleteCommentById(NOT_EXISTING_COMMENT_ID);
        assertThat(result).contains(FAILED_TO_DELETE_COMMENT);
    }

    @Test
    @DisplayName("Должны отобразить сообщение об ошибке при получении книги по идентификатору")
    void shouldShowErrMsgOnUpdateBookException(){
        given(bookService.update(anyLong(), anyString(), anyString(), anyLong(), anyLong())).willThrow(RuntimeException.class);
        String result = bookShellCommands.updateBook(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID, EXISTING_DESCRIPTION);
        verify(bookService).update(EXISTING_BOOK_ID, EXISTING_BOOK_NAME, EXISTING_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);
        assertThat(result).contains(BookShellCommands.FAILED_TO_UPDATE_BOOK);
    }
}
