package ru.otus.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.exception.AuthorValidationException;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.exception.GenreValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тестирование валидатора объектов библиотеки")
@SpringBootTest
class LibraryObjectValidatorTest {
    @Autowired
    private LibraryObjectValidator validator;

    private static final String NEW_BOOK_NAME = "new_book_name";
    private static final Long EXISTING_AUTHOR_ID = 2L;
    private static final Long WRONG_AUTHOR_ID = -1L;
    private static final Long WRONG_GENRE_ID = -1L;
    private static final Long WRONG_BOOK_ID = -1L;
    private static final Long EXISTING_GENRE_ID = 3L;
    private static final String NEW_DESCRIPTION = "new_description";

    @Test
    @DisplayName("Должна выдаваться ошибка при валидации книги без автора")
    void shouldFailOnEmptyAuthorInsert(){
        assertThrows(BookValidationException.class, () -> validator.validateBook(getBookWithoutAuthor()));
    }

    private Book getBookWithoutAuthor(){
        return new Book(NEW_BOOK_NAME, null, new Genre(EXISTING_GENRE_ID), NEW_DESCRIPTION);
    }

    @Test
    @DisplayName("Должна выдаваться ошибка при валидации книги без жанра")
    void shouldFailOnEmptyGenreIDInsert(){
        assertThrows(BookValidationException.class, () -> validator.validateBook(
                new Book(NEW_BOOK_NAME, new Author(EXISTING_AUTHOR_ID), null, NEW_DESCRIPTION)
        ));
    }

    @Test
    @DisplayName("Должны выдать ошибку при валидации книги с некорректным идентификатором жанра")
    void shouldFailOnInsertWithIncorrectGenreId(){
        assertThrows(GenreValidationException.class, () -> validator.validateGenre(new Genre(WRONG_GENRE_ID)));
    }

    @Test
    @DisplayName("Должны выбросить ошибку при валидации книги с отрицательным идентификатором")
    void shouldThrowExOnNegativeBookId(){
        assertThrows(BookValidationException.class, () -> validator.validateBookForUpdate(
                new Book(WRONG_BOOK_ID, NEW_BOOK_NAME, new Author(EXISTING_AUTHOR_ID), new Genre(EXISTING_GENRE_ID), NEW_DESCRIPTION)
        ));
    }

    @Test
    @DisplayName("Должны выдать ошибку валидации книги с некорректным идентификатором автора")
    void shouldFailOnInsertWithIncorrectAuthorId(){
        assertThrows(AuthorValidationException.class, () -> validator.validateAuthor(new Author(WRONG_AUTHOR_ID)));
    }

    @Test
    @DisplayName("Должна выдаваться ошибка при валидации книги без имени")
    void shouldFailOnEmptyBookNameInsert(){
        assertThrows(BookValidationException.class, () -> validator.validateBook(
                new Book(null, new Author(WRONG_AUTHOR_ID), new Genre(EXISTING_GENRE_ID), NEW_DESCRIPTION)
        ));
    }
}
