package ru.otus.spring.dao;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тестирование репозитория для работы с комментариями книги")
@DataJpaTest
class BookCommentRepositoryTest {
    private static final Long EXISTING_BOOK_ID = 4L;
    private static final Integer EXPECTED_COMMENTS_COUNT = 4;
    private static final Long NOT_EXISTING_BOOK_ID = 4000L;

    @Autowired
    private BookCommentRepository commentRepository;
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Должны найти комментарии по идентификатору книги")
    void shouldFindByBookId(){
        List<BookComment> comments = commentRepository.findAllByBookId(EXISTING_BOOK_ID);
        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT);

        comments = commentRepository.findAllByBookId(NOT_EXISTING_BOOK_ID);
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("Должны сохранить новый комментарий для книги")
    void shouldSaveNew(){
        List<BookComment>  comments = commentRepository.findAllByBookId(EXISTING_BOOK_ID);

        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT);

        commentRepository.save( new BookComment(0, new Book(EXISTING_BOOK_ID), "test_user", "test_comment"));
        comments = commentRepository.findAllByBookId(EXISTING_BOOK_ID);

        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT + 1);
        comments.forEach(c -> assertThat(c.getBook().getId() == EXISTING_BOOK_ID));


    }

    @Test
    @DisplayName("Должны выбросить ошибку при попытке сохранить комментарий для несуществующей книги")
    void shouldThrowIfSaveCommentForNotExistingBook(){
        assertThrows(
                DataIntegrityViolationException.class,
                () -> commentRepository.save( new BookComment(0, new Book(NOT_EXISTING_BOOK_ID), "test_user", "test_comment"))
        );
    }

    @Test
    @DisplayName("Должны удалить комментарий по его идентификатору")
    void shouldDeleteCommentById(){
        List<BookComment> comments = commentRepository.findAllByBookId(EXISTING_BOOK_ID);
        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT);

        commentRepository.deleteById(comments.get(0).getId());
        assertThat(commentRepository.findAllByBookId(EXISTING_BOOK_ID)).hasSize(EXPECTED_COMMENTS_COUNT - 1);
    }

    @Test
    @DisplayName("Должны удалить комментарии по идентификатору книги")
    void shouldDeleteCommentsByBookId(){
        List<BookComment> comments = commentRepository.findAllByBookId(EXISTING_BOOK_ID);
        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT);

        commentRepository.deleteAllByBookId(EXISTING_BOOK_ID);
        comments = commentRepository.findAllByBookId(EXISTING_BOOK_ID);
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("Не должны удалить комментарии по несуществующему идентификатору книги")
    void shouldDeleteCommentsByNotExistingBookId(){
        List<BookComment> comments = commentRepository.findAll();
        int sizeBefore = comments.size();

        commentRepository.deleteAllByBookId(NOT_EXISTING_BOOK_ID);
        comments = commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(sizeBefore);
    }
}
