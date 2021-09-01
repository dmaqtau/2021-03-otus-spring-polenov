package ru.otus.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.dao.BookCommentRepository;
import ru.otus.spring.domain.BookComment;
import ru.otus.spring.dto.BookCommentDTO;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.exception.CommentValidationException;
import ru.otus.spring.exception.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@DisplayName("Тестирование сервиса BookCommentService для работы с комментариями для книгами")
@SpringBootTest
class BookCommentServiceImplTest {
    @MockBean
    private LibraryObjectValidator validator;
    @MockBean
    private BookCommentRepository commentRepository;
    @MockBean
    private BookService bookService;
    @Autowired
    private BookCommentService commentService;

    @Captor
    private ArgumentCaptor<BookComment> bookCommentCaptor;

    private static final Long EXISTING_BOOK_ID = 4L;
    private static final Long INVALID_BOOK_ID = -5L;
    private static final Long NOT_EXISTING_BOOK_ID = 4000L;
    private static final String COMMENT_LOGIN = "test_login";
    private static final String COMMENT_VALUE = "test_comment";

    @Test
    @DisplayName("Должны добавить комментарий для книги")
    void shouldAddCommentForBook() {
        BookCommentDTO dto = new BookCommentDTO(EXISTING_BOOK_ID, COMMENT_LOGIN, COMMENT_VALUE);
        doNothing().when(bookService).checkBookExists(EXISTING_BOOK_ID);

        commentService.addComment(dto);
        verify(commentRepository).save(bookCommentCaptor.capture());

        BookComment savedComment = bookCommentCaptor.getValue();
        assertAll(
                () -> assertThat(savedComment).isNotNull(),
                () -> assertThat(savedComment.getId()).isNotNull(),
                () -> assertThat(savedComment.getUserLogin()).isEqualTo(dto.getUserLogin()),
                () -> assertThat(savedComment.getComment()).isEqualTo(dto.getComment()),
                () -> assertThat(savedComment.getBook().getId()).isEqualTo(EXISTING_BOOK_ID)
        );
    }

    @Test
    @DisplayName("Должны выбросить ошибку при попытке добавления комментария к несуществующей книге")
    void shouldThrowOnAddingCommentForNotExistingBook(){
        doThrow(new NotFoundException("ex")).when(bookService).checkBookExists(NOT_EXISTING_BOOK_ID);
        final BookCommentDTO dto = new BookCommentDTO(NOT_EXISTING_BOOK_ID, COMMENT_LOGIN, COMMENT_VALUE);
        assertThrows(NotFoundException.class, () -> commentService.addComment(dto));
    }

    @Test
    @DisplayName("Должны выбросить ошибку при попытке добавления комментария с некорректным ID книги")
    void shouldThrowOnAddingCommentWithInvalidBookID(){
        doThrow(new BookValidationException("ex")).when(bookService).checkBookExists(INVALID_BOOK_ID);
        final BookCommentDTO dto = new BookCommentDTO(INVALID_BOOK_ID, COMMENT_LOGIN, COMMENT_VALUE);
        assertThrows(BookValidationException.class, () -> commentService.addComment(dto));
    }

    @Test
    @DisplayName("Должны выбросить ошибку при попытке добавления комментария без обязательных полей")
    void shouldThrowOnAddingCommentWithEmptyFields(){
        doNothing().when(bookService).checkBookExists(EXISTING_BOOK_ID);

        final BookCommentDTO dto = new BookCommentDTO(EXISTING_BOOK_ID, "  ", COMMENT_VALUE);
        doThrow(new CommentValidationException("ex")).when(validator).validateBookComment(dto);
        assertThrows(CommentValidationException.class, () -> commentService.addComment(dto));

        final BookCommentDTO anotherDto = new BookCommentDTO(EXISTING_BOOK_ID, COMMENT_LOGIN, "");
        doThrow(new CommentValidationException("ex")).when(validator).validateBookComment(anotherDto);
        assertThrows(CommentValidationException.class, () -> commentService.addComment(anotherDto));
    }

    @Test
    @DisplayName("Должны выбросить ошибку при попытке удаления комментариев у несуществующей книги")
    void shouldThrowOnDeletingCommentsForNotExistingBook(){
        doThrow(new NotFoundException("ex")).when(bookService).checkBookExists(NOT_EXISTING_BOOK_ID);
        assertThrows(NotFoundException.class, () -> commentService.deleteCommentsByBookId(NOT_EXISTING_BOOK_ID));
    }

    @Test
    @DisplayName("Должны выбросить ошибку при попытке удаления комментариев с некорректным ID книги")
    void shouldThrowOnDeletingCommentsWithInvalidBookID(){
        doThrow(new BookValidationException("ex")).when(bookService).checkBookExists(INVALID_BOOK_ID);
        assertThrows(BookValidationException.class, () -> commentService.deleteCommentsByBookId(INVALID_BOOK_ID));
    }
}
