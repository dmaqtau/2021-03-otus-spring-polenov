package ru.otus.spring.service;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import ru.otus.spring.dao.BookDao;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.exception.AuthorValidationException;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.exception.GenreValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("Тестирование сервиса BookService для работы с книгами")
@SpringBootTest
class BookServiceImplTest {
    private static final String NEW_BOOK_NAME = "new_book_name";
    private static final Long EXISTING_AUTHOR_ID = 2L;
    private static final Long WRONG_AUTHOR_ID = -1L;
    private static final Long WRONG_GENRE_ID = -1L;
    private static final Long WRONG_BOOK_ID = -1L;
    private static final Long EXISTING_GENRE_ID = 3L;
    private static final Long EXISTING_BOOK_ID = 4L;
    private static final String NEW_DESCRIPTION = "new_description";

    @Mock
    private BookDao bookDao;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Должна выдаваться ошибка при попытке вставить книгу без жанра")
    void shouldFailOnEmptyGenreIDInsert(){
        assertThrows(GenreValidationException.class, () -> bookService.create(NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, 0));
    }

    @Test
    @DisplayName("Должна выдаваться ошибка при попытке вставить книгу без имени")
    void shouldFailOnEmptyBookNameInsert(){
        assertThrows(BookValidationException.class, () -> bookService.create(null, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID));
    }

    @Test
    @DisplayName("Должна выдаваться ошибка при попытке вставить книгу без автора")
    void shouldFailOnEmptyAuthorInsert(){
        assertThrows(AuthorValidationException.class, () -> bookService.create(NEW_BOOK_NAME, NEW_DESCRIPTION, 0, EXISTING_GENRE_ID));
    }

    @Test
    @DisplayName("Должно выполниться добавление новой книги")
    void shouldSuccessfullyInsertBook(){
        final long newId = 1L;

        Book expectedBook = Book.builder()
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID).build())
                .description(NEW_DESCRIPTION)
                .genre(Genre.builder().id(EXISTING_GENRE_ID).build())
                .build();

        Book newBook = new Book();
        BeanUtils.copyProperties(expectedBook, newBook);
        newBook.setId(newId);

        given(bookDao.insert(expectedBook)).willReturn(newBook);
        Book actualBook = bookService.create(NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);

        assertAll(
                () -> verify(bookDao).insert(expectedBook),
                () -> assertThat(actualBook).isEqualTo(newBook)
        );
    }

    @Test
    @DisplayName("Должны выдать ошибку при попытке поиска по некорректному идентификатору")
    void shouldFailOnGetByIncorrectId(){
        assertThrows(IllegalArgumentException.class, () -> bookService.getByID(0));
        assertThrows(IllegalArgumentException.class, () -> bookService.getByID(-1));
    }

    @Test
    @DisplayName("Должны выдать ошибку при попытке удаления по некорректному идентификатору")
    void shouldFailOnDeleteByIncorrectId(){
        assertThrows(IllegalArgumentException.class, () -> bookService.deleteByID(0));
        assertThrows(IllegalArgumentException.class, () -> bookService.deleteByID(-1));
    }

    @Test
    @DisplayName("Должны выдать ошибку при попытке сохранения книги с некорректным идентификатором автора")
    void shouldFailOnInsertWithIncorrectAuthorId(){
        assertThrows(AuthorValidationException.class, () -> bookService.create(NEW_BOOK_NAME, NEW_DESCRIPTION, WRONG_AUTHOR_ID, EXISTING_GENRE_ID));
    }

    @Test
    @DisplayName("Должны выдать ошибку при попытке сохранения книги с некорректным идентификатором жанра")
    void shouldFailOnInsertWithIncorrectGenreId(){
        assertThrows(GenreValidationException.class, () -> bookService.create(NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, WRONG_GENRE_ID));
    }

    @Test
    @DisplayName("Должны удалить книгу по идентификатору")
    void shouldDeleteBookById(){
        int expected = 1;
        given(bookDao.deleteById(EXISTING_BOOK_ID)).willReturn(expected);

        int actual = bookService.deleteByID(EXISTING_BOOK_ID);
        assertAll(
                () -> verify(bookDao).deleteById(EXISTING_BOOK_ID),
                () -> assertThat(actual).isEqualTo(expected)
        );
    }

    @Test
    @DisplayName("Должны вернуть книгу по идентификатору")
    void shouldReturnBookById(){

        Book expectedBook = Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID).build())
                .description(NEW_DESCRIPTION)
                .genre(Genre.builder().id(EXISTING_GENRE_ID).build())
                .build();

        given(bookDao.getById(EXISTING_BOOK_ID)).willReturn(expectedBook);
        Book actualBook = bookService.getByID(EXISTING_BOOK_ID);

        assertAll(
                () -> verify(bookDao).getById(EXISTING_BOOK_ID),
                () -> assertThat(actualBook).isEqualTo(expectedBook)
        );
    }

    @Test
    @DisplayName("Должны вернуть все книги")
    void shouldGetAllBooks(){

        Book expectedBookFirst = Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID).build())
                .description(NEW_DESCRIPTION)
                .genre(Genre.builder().id(EXISTING_GENRE_ID).build())
                .build();

        Book expectedBookSecond = Book.builder()
                .id(EXISTING_BOOK_ID + 1)
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID).build())
                .description(NEW_DESCRIPTION)
                .genre(Genre.builder().id(EXISTING_GENRE_ID).build())
                .build();

        given(bookDao.getAll()).willReturn(List.of(expectedBookFirst, expectedBookSecond));
        List<Book> actualBooks = bookService.getAll();

        assertAll(
                () -> assertThat(actualBooks).isNotEmpty(),
                () -> assertThat(actualBooks).contains(expectedBookFirst, expectedBookSecond)
        );
    }

    @Test
    @DisplayName("Должны обновить информацию о книге")
    void shouldUpdateBook(){

        Book expectedBook = Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID).build())
                .description(NEW_DESCRIPTION)
                .genre(Genre.builder().id(EXISTING_GENRE_ID).build())
                .build();

        given(bookDao.update(expectedBook)).willReturn(expectedBook);
        Book actualBook = bookService.update(
                expectedBook.getId(),
                expectedBook.getBookName(),
                expectedBook.getDescription(),
                expectedBook.getAuthor().getId(),
                expectedBook.getGenre().getId()
        );

        assertAll(
                () -> verify(bookDao).update(expectedBook),
                () -> assertThat(actualBook).isEqualTo(expectedBook)
        );
    }

    @Test
    @DisplayName("Должны выбросить ошибку при попытке обновить книгу по отрицательному идентификатору")
    void shouldThrowExOnNegativeBookId(){
        assertThrows(BookValidationException.class, () -> bookService.update(WRONG_BOOK_ID, NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID));
    }

    @Test
    @DisplayName("Не должна измениться информация о книге, если не переданы значения полей для обновления")
    void shouldUpdateBookIfNoDataProvided(){

        Book emptyBookObject = Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName("")
                .author(null)
                .description("")
                .genre(null)
                .build();

        Book existingBook = Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID).build())
                .description(NEW_DESCRIPTION)
                .genre(Genre.builder().id(EXISTING_GENRE_ID).build())
                .build();

        given(bookDao.update(emptyBookObject)).willReturn(existingBook);

        Book actualBook = bookService.update(
                emptyBookObject.getId(),
                emptyBookObject.getBookName(),
                emptyBookObject.getDescription(),
                0, 0
        );

        assertAll(
                () -> verify(bookDao).update(emptyBookObject),
                () -> assertThat(actualBook).isEqualTo(existingBook)
        );
    }
}
