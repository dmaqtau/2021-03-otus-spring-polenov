package ru.otus.spring.dao;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тестирование репозитория для работы с книгами")
@DataJpaTest
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private BookCommentRepository bookCommentRepository;

    private static final String NEW_BOOK_NAME = "new_book_name";
    private static final Long EXISTING_AUTHOR_ID = 3L;

    private static final String EXISTING_AUTHOR_NAME= "Зиро";
    private static final String EXISTING_AUTHOR_SURNAME= "Саб";
    private static final String EXISTING_AUTHOR_PATRONYMIC = "Тест";

    private static final Long EXISTING_GENRE_ID = 3L;
    private static final Long EXISTING_BOOK_ID = 4L;
    private static final Long NOT_EXISTING_BOOK_ID = 4000L;

    private static final Long EXPECTED_BOOKS_COUNT = 6L;
    private static final Integer EXPECTED_COMMENTS_SIZE = 4;
    private static final String NEW_DESCRIPTION = "new_description";

    @Test
    @DisplayName("Должна успешно добавиться новая книга")
    void shouldSuccessfullyInsertNewBook(){
        Book expectedBook = new Book(
                NEW_BOOK_NAME,
                authorRepository.findById(EXISTING_AUTHOR_ID).orElse(null),
                genreRepository.findById(EXISTING_GENRE_ID).orElse(null), NEW_DESCRIPTION);
        Book actualBook = bookRepository.findById(bookRepository.save(expectedBook).getId()).orElse(null);

        assertAll(
                () -> assertThat(actualBook).isNotNull(),
                () -> assertThat(actualBook.getId()).isPositive(),
                () -> assertThat(actualBook.getAuthor()).isNotNull(),
                () -> assertAuthorInfo(actualBook, true),
                () -> assertThat(actualBook.getGenre()).isNotNull(),
                () -> assertThat(actualBook.getName()).isEqualTo(expectedBook.getName()),
                () -> assertThat(actualBook.getDescription()).isEqualTo(expectedBook.getDescription()),
                () -> assertThat(actualBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId()),
                () -> assertThat(actualBook.getGenre().getId()).isEqualTo(expectedBook.getGenre().getId())
        );
    }

    @Test
    @DisplayName("Должна вернуться существующая книга по идентификатору")
    void shouldReturnBookByExistingID(){
        Book actualBook = bookRepository.findById(EXISTING_BOOK_ID).orElse(null);

        assertAll(
                () -> assertThat(actualBook).isNotNull(),
                () -> assertThat(actualBook.getAuthor()).isNotNull(),
                () -> assertThat(actualBook.getGenre()).isNotNull(),
                () -> assertThat(actualBook.getName()).isNotNull(),
                () -> assertThat(actualBook.getDescription()).isNotNull(),
                () -> assertThat(actualBook.getId()).isPositive()
        );
    }

    @Test
    @DisplayName("Должна успешно обновиться существующая книга")
    void shouldSuccessfullyUpdateExistingBook(){
        Book existingBook = bookRepository.findById(EXISTING_BOOK_ID).orElse(null);
        assertAuthorInfo(existingBook, true);

        existingBook.setGenre(null);
        existingBook.setDescription("test");

        Author newAuthor = authorRepository.findById(2L).orElse(null);
        existingBook.setAuthor(newAuthor);

        Book updatedBook = bookRepository.save(existingBook);
        assertAll(
                () -> assertAuthorInfo(updatedBook, false),
                () -> assertThat(updatedBook.getDescription()).isEqualTo("test"),
                () -> assertThat(updatedBook.getGenre()).isNull()
        );
    }

    @Test
    @DisplayName("Должны получить правильное количество книг")
    void shouldGetCorrectBooksCount() {
        Long actualBooksCount = bookRepository.count();
        assertThat(actualBooksCount).isNotNull().isEqualTo(EXPECTED_BOOKS_COUNT);
    }

    @Test
    @DisplayName("Должны получить все книги")
    void shouldRetrieveAllBooks() {
        List<Book> allBooksActual = bookRepository.findAll();
        assertThat(allBooksActual)
                .hasSize(EXPECTED_BOOKS_COUNT.intValue())
                .doesNotContainNull();
    }

    @Test
    @DisplayName("Должны успешно удалить одну книгу по идентификатору")
    void shouldSuccessfullyDeleteSingleBookById(){
        List<Book> allBooksActual = bookRepository.findAll();
        assertThat(allBooksActual).isNotEmpty();

        int sizeBeforeDelete = allBooksActual.size();

        Book bookToDelete = allBooksActual.get(0);
        bookRepository.deleteById(bookToDelete.getId());

        allBooksActual = bookRepository.findAll();
        assertThat(allBooksActual).hasSize(sizeBeforeDelete - 1);
    }

    @Test
    @DisplayName("Должна быть корректно выполнена проверка существования книги")
    void shouldCheckBookExistince(){
        assertTrue(bookRepository.existsById(EXISTING_BOOK_ID));
        assertFalse(bookRepository.existsById(NOT_EXISTING_BOOK_ID));
    }

    @Test
    @DisplayName("Должны удалить комментарии к книге вместе с книгой")
    void shouldDeleteBothBookAndComments(){
        Book actualBook = bookRepository.findById(EXISTING_BOOK_ID).orElse(null);

        List<BookComment> commentsBefore = bookCommentRepository.findAllByBookId(EXISTING_BOOK_ID);
        assertThat(commentsBefore).hasSize(EXPECTED_COMMENTS_SIZE);

        bookRepository.deleteById(EXISTING_BOOK_ID);
        assertThat(bookRepository.findById(EXISTING_BOOK_ID)).isEmpty();

        List<BookComment> commentsAfter = bookCommentRepository.findAllByBookId(EXISTING_BOOK_ID);
        assertThat(commentsAfter).isEmpty();
    }

    private static void assertAuthorInfo(Book actualBook, boolean isEqual){
        if(isEqual){
            assertAll(
                    () -> assertThat(actualBook.getAuthor().getName()).isNotNull().isEqualTo(EXISTING_AUTHOR_NAME),
                    () -> assertThat(actualBook.getAuthor().getSurname()).isNotNull().isEqualTo(EXISTING_AUTHOR_SURNAME),
                    () -> assertThat(actualBook.getAuthor().getPatronymic()).isEqualTo(EXISTING_AUTHOR_PATRONYMIC)
            );
        } else {
            assertAll(
                    () -> assertThat(actualBook.getAuthor().getName()).isNotNull().isNotEqualTo(EXISTING_AUTHOR_NAME),
                    () -> assertThat(actualBook.getAuthor().getSurname()).isNotNull().isNotEqualTo(EXISTING_AUTHOR_SURNAME),
                    () -> assertThat(actualBook.getAuthor().getPatronymic()).isNotEqualTo(EXISTING_AUTHOR_PATRONYMIC)
            );
        }

    }
}