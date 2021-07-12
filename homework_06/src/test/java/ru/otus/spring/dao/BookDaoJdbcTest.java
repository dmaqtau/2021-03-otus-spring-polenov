package ru.otus.spring.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тестирование Dao для работы с книгами")
@JdbcTest
@Import(BookDaoJdbc.class)
class BookDaoJdbcTest {
    @Autowired
    private BookDao bookDao;

    private static final String NEW_BOOK_NAME = "new_book_name";
    private static final Long EXISTING_AUTHOR_ID = 2L;
    private static final Long NOT_EXISTING_AUTHOR_ID = 2000L;

    private static final String EXISTING_AUTHOR_NAME= "Такеда";
    private static final String EXISTING_AUTHOR_SURNAME= "Ю";
    private static final String EXISTING_AUTHOR_PATRONYMIC = "Ноунеймович";

    private static final Long EXISTING_GENRE_ID = 3L;
    private static final Long EXISTING_BOOK_ID = 4L;
    private static final Integer EXPECTED_BOOKS_COUNT = 6;
    private static final String NEW_DESCRIPTION = "new_description";

    @Test
    @Transactional
    @DisplayName("Должна успешно добавиться новая книга")
    void shouldSuccessfullyInsertNewBook(){
        Book expectedBook = Book.builder()
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID).build())
                .description(NEW_DESCRIPTION)
                .genre(Genre.builder().id(EXISTING_GENRE_ID).build())
                .build();
        Book actualBook = bookDao.insert(expectedBook);

        assertAll(
                () -> assertThat(actualBook).isNotNull(),
                () -> assertThat(actualBook.getId()).isPositive(),
                () -> assertThat(actualBook.getAuthor()).isNotNull(),
                () -> assertAuthorInfo(actualBook, true),
                () -> assertThat(actualBook.getGenre()).isNotNull(),
                () -> assertThat(actualBook.getBookName()).isEqualTo(expectedBook.getBookName()),
                () -> assertThat(actualBook.getDescription()).isEqualTo(expectedBook.getDescription()),
                () -> assertThat(actualBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId()),
                () -> assertThat(actualBook.getGenre().getId()).isEqualTo(expectedBook.getGenre().getId())
        );
    }

    @Test
    @DisplayName("Должна вернуться существующая книга по идентификатору")
    void shouldReturnBookByExistingID(){
        Book actualBook = bookDao.getById(EXISTING_BOOK_ID);

        assertAll(
                () -> assertThat(actualBook).isNotNull(),
                () -> assertThat(actualBook.getAuthor()).isNotNull(),
                () -> assertThat(actualBook.getGenre()).isNotNull(),
                () -> assertThat(actualBook.getBookName()).isNotNull(),
                () -> assertThat(actualBook.getDescription()).isNotNull(),
                () -> assertThat(actualBook.getId()).isPositive()
        );
    }

    @Test
    @Transactional
    @DisplayName("Должна успешно обновиться существующая книга")
    void shouldSuccessfullyUpdateExistingBook(){
        Book existingBook = bookDao.getById(EXISTING_BOOK_ID);
        assertAuthorInfo(existingBook, false);

        Book bookUpdateInfo = Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(EXISTING_AUTHOR_ID).build())
                .build();
        Book updatedBook = bookDao.update(bookUpdateInfo);
        assertAuthorInfo(updatedBook, true);
    }

    @Test
    @DisplayName("Должна быть выдана ошибка при попытке обновить книгу с на несуществующий ID автора")
    void shouldFailOnUpdatingWithNotExistingAuthorId(){
        Book existingBook = bookDao.getById(EXISTING_BOOK_ID);
        assertAuthorInfo(existingBook, false);

        Book bookUpdateInfo = Book.builder()
                .id(EXISTING_BOOK_ID)
                .bookName(NEW_BOOK_NAME)
                .author(Author.builder()
                        .id(NOT_EXISTING_AUTHOR_ID).build())
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> bookDao.update(bookUpdateInfo));
    }

    @Test
    @DisplayName("Должны получить правильное количество книг")
    void shouldGetCorrectBooksCount() {
        Integer actualBooksCount = bookDao.count();
        assertThat(actualBooksCount).isNotNull().isEqualTo(EXPECTED_BOOKS_COUNT);
    }

    @Test
    @DisplayName("Должны получить все книги")
    void shouldRetrieveAllBooks() {
        List<Book> allBooksActual = bookDao.getAll();
        assertThat(allBooksActual)
                .hasSize(EXPECTED_BOOKS_COUNT)
                .doesNotContainNull();
    }

    @Test
    @Transactional
    @DisplayName("Должны успешно удалить одну книгу по идентификатору")
    void shouldSuccessfullyDeleteSingleBookById(){
        List<Book> allBooksActual = bookDao.getAll();
        assertThat(allBooksActual).isNotEmpty();

        int sizeBeforeDelete = allBooksActual.size();

        Book bookToDelete = allBooksActual.get(0);
        int result = bookDao.deleteById(bookToDelete.getId());
        assertThat(result).isEqualTo(1);

        allBooksActual = bookDao.getAll();
        assertThat(allBooksActual).hasSize(sizeBeforeDelete - 1);
    }

    private static void assertAuthorInfo(Book actualBook, boolean isEqual){
        if(isEqual){
            assertAll(
                    () -> assertThat(actualBook.getAuthor().getAuthorName()).isNotNull().isEqualTo(EXISTING_AUTHOR_NAME),
                    () -> assertThat(actualBook.getAuthor().getSurname()).isNotNull().isEqualTo(EXISTING_AUTHOR_SURNAME),
                    () -> assertThat(actualBook.getAuthor().getPatronymic()).isNotNull().isEqualTo(EXISTING_AUTHOR_PATRONYMIC)
            );
        } else {
            assertAll(
                    () -> assertThat(actualBook.getAuthor().getAuthorName()).isNotNull().isNotEqualTo(EXISTING_AUTHOR_NAME),
                    () -> assertThat(actualBook.getAuthor().getSurname()).isNotNull().isNotEqualTo(EXISTING_AUTHOR_SURNAME),
                    () -> assertThat(actualBook.getAuthor().getPatronymic()).isNotEqualTo(EXISTING_AUTHOR_PATRONYMIC)
            );
        }

    }
}