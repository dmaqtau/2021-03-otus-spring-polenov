package ru.otus.spring.dao;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестирование репозитория для работы с комментариями книги")
@DataJpaTest
@Import({BookCommentRepositoryJpa.class, BookRepositoryJpa.class})
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
        List<BookComment> comments = commentRepository.findByBookId(EXISTING_BOOK_ID);
        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT);

        comments = commentRepository.findByBookId(NOT_EXISTING_BOOK_ID);
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("Должны сохранить новый комментарий для книги")
    void shouldSaveNew(){
        Book existingBook = bookRepository.findById(EXISTING_BOOK_ID);
        List<BookComment>  comments = commentRepository.findByBookId(EXISTING_BOOK_ID);

        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT);

        commentRepository.save("test_user", "test_comment", EXISTING_BOOK_ID);
        comments = commentRepository.findByBookId(EXISTING_BOOK_ID);

        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT + 1);
        comments.forEach(c -> assertThat(c.getBook().getId() == EXISTING_BOOK_ID));
    }

    @Test
    @DisplayName("Должны удалить комментарий по его идентификатору")
    void shouldDeleteCommentById(){
        List<BookComment> comments = commentRepository.findByBookId(EXISTING_BOOK_ID);
        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT);

        commentRepository.deleteById(comments.get(0).getId());
        assertThat(commentRepository.findByBookId(EXISTING_BOOK_ID)).hasSize(EXPECTED_COMMENTS_COUNT - 1);
    }

    @Test
    @DisplayName("Должны удалить комментарии идентификатору книги")
    void shouldDeleteCommentsByBookId(){
        List<BookComment> comments = commentRepository.findByBookId(EXISTING_BOOK_ID);
        assertThat(comments).hasSize(EXPECTED_COMMENTS_COUNT);

        commentRepository.deleteByBookId(EXISTING_BOOK_ID);
        comments = commentRepository.findByBookId(EXISTING_BOOK_ID);
        assertThat(comments).isEmpty();
    }
}
