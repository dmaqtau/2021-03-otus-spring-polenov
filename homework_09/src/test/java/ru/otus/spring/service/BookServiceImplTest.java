package ru.otus.spring.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.dao.AuthorRepository;
import ru.otus.spring.dao.BookRepository;
import ru.otus.spring.dao.GenreRepository;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.dto.BookDTO;
import ru.otus.spring.exception.BookValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("Тестирование сервиса BookService для работы с книгами")
@SpringBootTest
class BookServiceImplTest {
    private static final String NEW_BOOK_NAME = "new_book_name";
    private static final Long EXISTING_AUTHOR_ID = 2L;
    private static final Long EXISTING_GENRE_ID = 3L;
    private static final Long EXISTING_BOOK_ID = 4L;
    private static final String NEW_DESCRIPTION = "new_description";

    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private LibraryObjectValidator validator;
    @MockBean
    private AuthorRepository authorRepository;
    @MockBean
    private GenreRepository genreRepository;
    @Autowired
    private BookServiceImpl bookService;

    @BeforeEach
    void init(){
        given(bookRepository.existsById(EXISTING_BOOK_ID)).willReturn(true);
    }

    @Test
    @DisplayName("Должно выполниться добавление новой книги")
    void shouldSuccessfullyInsertBook(){
        final long newId = 1L;

        Book expectedBook = new Book(NEW_BOOK_NAME,
                getExpectedAuthor(),
                getExpectedGenre(), NEW_DESCRIPTION
        );

        Book newBook = new Book();
        BeanUtils.copyProperties(expectedBook, newBook);
        newBook.setId(newId);

        given(authorRepository.findById(EXISTING_AUTHOR_ID)).willReturn(Optional.of(getExpectedAuthor()));
        given(genreRepository.findById(EXISTING_GENRE_ID)).willReturn(Optional.of(getExpectedGenre()));
        given(bookRepository.save(expectedBook)).willReturn(newBook);

        BookDTO bookDTO = new BookDTO(null, NEW_BOOK_NAME, NEW_DESCRIPTION, EXISTING_AUTHOR_ID, EXISTING_GENRE_ID);
        Book actualBook = bookService.create(bookDTO);

        assertAll(
                () -> verify(bookRepository).save(expectedBook),
                () -> assertThat(actualBook.getName()).isEqualTo(newBook.getName()),
                () -> assertThat(actualBook.getDescription()).isEqualTo(newBook.getDescription()),
                () -> assertThat(actualBook.getAuthor().getId()).isEqualTo(newBook.getAuthor().getId()),
                () -> assertThat(actualBook.getGenre().getId()).isEqualTo(newBook.getGenre().getId())
        );
    }

    @Test
    @DisplayName("Должны выдать ошибку при попытке поиска по некорректному идентификатору")
    void shouldFailOnGetByIncorrectId(){
        assertThrows(BookValidationException.class, () -> bookService.findByID(0));
        assertThrows(BookValidationException.class, () -> bookService.findByID(-1));
    }

    @Test
    @DisplayName("Должны выдать ошибку при попытке удаления по некорректному идентификатору")
    void shouldFailOnDeleteByIncorrectId(){
        assertThrows(BookValidationException.class, () -> bookService.deleteByID(0));
        assertThrows(BookValidationException.class, () -> bookService.deleteByID(-1));
    }

    @Test
    @DisplayName("Должны удалить книгу по идентификатору")
    void shouldDeleteBookById(){
        bookService.deleteByID(EXISTING_BOOK_ID);

        assertAll(
                () -> verify(bookRepository).deleteById(EXISTING_BOOK_ID)
        );
    }

    @Test
    @DisplayName("Должны вернуть книгу по идентификатору")
    void shouldReturnBookById(){
        Book expectedBook = new Book(EXISTING_BOOK_ID, NEW_BOOK_NAME, new Author(EXISTING_AUTHOR_ID), new Genre(EXISTING_GENRE_ID), NEW_DESCRIPTION);

        given(bookRepository.findById(EXISTING_BOOK_ID)).willReturn(Optional.of(expectedBook));
        Book actualBook = bookService.findByID(EXISTING_BOOK_ID);

        assertAll(
                () -> verify(bookRepository).findById(EXISTING_BOOK_ID),
                () -> assertThat(actualBook).isEqualTo(expectedBook)
        );
    }

    @Test
    @DisplayName("Должны вернуть все книги")
    void shouldGetAllBooks(){

        Book expectedBookFirst = new Book(EXISTING_BOOK_ID, NEW_BOOK_NAME, new Author(EXISTING_AUTHOR_ID), new Genre(EXISTING_GENRE_ID), NEW_DESCRIPTION);
        Book expectedBookSecond = new Book(EXISTING_BOOK_ID + 1, NEW_BOOK_NAME, new Author(EXISTING_AUTHOR_ID), new Genre(EXISTING_GENRE_ID), NEW_DESCRIPTION);


        given(bookRepository.findAll()).willReturn(List.of(expectedBookFirst, expectedBookSecond));
        List<Book> actualBooks = bookService.getAll();

        assertAll(
                () -> assertThat(actualBooks).isNotEmpty(),
                () -> assertThat(actualBooks).contains(expectedBookFirst, expectedBookSecond)
        );
    }

    @Test
    @DisplayName("Должны обновить информацию о книге")
    void shouldUpdateBook(){
        Book expectedBook = new Book(EXISTING_BOOK_ID, NEW_BOOK_NAME, getExpectedAuthor(), getExpectedGenre(), NEW_DESCRIPTION);
        given(bookRepository.save(expectedBook)).willReturn(expectedBook);

        Book existingBook = new Book();
        existingBook.setId(EXISTING_BOOK_ID);
        given(bookRepository.findById(EXISTING_BOOK_ID)).willReturn(Optional.of(existingBook));

        given(authorRepository.findById(EXISTING_AUTHOR_ID)).willReturn(Optional.of(getExpectedAuthor()));
        given(genreRepository.findById(EXISTING_GENRE_ID)).willReturn(Optional.of(getExpectedGenre()));

        BookDTO bookDTO = new BookDTO(
                expectedBook.getId(),
                expectedBook.getName(),
                expectedBook.getDescription(),
                expectedBook.getAuthor().getId(),
                expectedBook.getGenre().getId());

        Book actualBook = bookService.update(bookDTO);

        assertAll(
                () -> verify(bookRepository).save(expectedBook),
                () -> assertThat(actualBook).isEqualTo(expectedBook)
        );
    }

    @Test
    @DisplayName("Не должна измениться информация о книге, если не переданы значения полей для обновления")
    void shouldUpdateBookIfNoDataProvided(){
        Book emptyBook = new Book(EXISTING_BOOK_ID, NEW_BOOK_NAME, null, null, "");
        Book existingBook = new Book(EXISTING_BOOK_ID, NEW_BOOK_NAME, getExpectedAuthor(), getExpectedGenre(), NEW_DESCRIPTION);
        Book bookToUpdate = new Book(EXISTING_BOOK_ID, NEW_BOOK_NAME, getExpectedAuthor(), getExpectedGenre(), "");

        given(bookRepository.save(bookToUpdate)).willReturn(existingBook);
        given(bookRepository.findById(EXISTING_BOOK_ID)).willReturn(Optional.of(existingBook));

        BookDTO bookDTO = new BookDTO(
                emptyBook.getId(),
                emptyBook.getName(),
                emptyBook.getDescription(),
                0L, 0L);

        Book actualBook = bookService.update(bookDTO);

        assertAll(
                () -> verify(bookRepository).save(bookToUpdate),
                () -> assertThat(actualBook).isEqualTo(bookToUpdate)
        );
    }

    private static Author getExpectedAuthor(){
        return new Author(EXISTING_AUTHOR_ID, "Ю", "Такеда", "Ноунеймович");
    }

    private static Genre getExpectedGenre(){
        return new Genre(EXISTING_GENRE_ID, "Фэнтези");
    }
}
