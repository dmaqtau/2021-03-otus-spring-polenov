package ru.otus.spring.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.spring.dao.AuthorRepository;
import ru.otus.spring.dao.BookRepository;
import ru.otus.spring.dao.GenreRepository;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.dto.BookDTO;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    private final LibraryObjectValidator validator;

    @Override
    @Transactional
    public Book create(BookDTO bookDTO) {
        Book book = getBookForSave(bookDTO);

        validator.validateBook(book);
        validator.validateAuthor(book.getAuthor());
        validator.validateGenre(book.getGenre());
        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Book findByID(long id) {
        checkBookIdValid(id);
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException(noBookMessage(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book update(BookDTO bookDTO) {
        validator.validateBookForUpdate(bookDTO);
        Book book = getBookForUpdate(bookDTO);

        if(book.getAuthor() != null){
            validator.validateAuthor(book.getAuthor());
        }

        if(book.getGenre() != null){
            validator.validateGenre(book.getGenre());
        }
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteByID(long id) {
        checkBookExists(id);
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkBookExists(long bookId){
        checkBookIdValid(bookId);

        if(!bookRepository.existsById(bookId)){
            throw new NotFoundException(noBookMessage(bookId));
        }
    }

    private Book getBookForUpdate(BookDTO bookDTO){
        Long bookId = bookDTO.getId();
        Long authorId = bookDTO.getAuthorId();
        Long genreId = bookDTO.getGenreId();

        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(noBookMessage(bookId)));

        if(existingBook == null){
            throw new NotFoundException(noBookMessage(bookId));
        }

        if(authorId != null && authorId > 0){
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new NotFoundException("Не найден автор по идентификатору: " + authorId));
            existingBook.setAuthor(author);
        }
        if(genreId != null && genreId > 0){
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new NotFoundException("Не найден жанр по идентификатору: " + genreId));
            existingBook.setGenre(genre);
        }
        existingBook.setName(bookDTO.getName());
        existingBook.setDescription(bookDTO.getDescription());
        return existingBook;
    }

    private Book getBookForSave(BookDTO bookDTO){
        Author author = null;
        Genre genre = null;

        Long authorId = bookDTO.getAuthorId();
        Long genreId = bookDTO.getGenreId();

        if(authorId > 0){
            author = authorRepository.findById(authorId).orElse(null);
            if(author == null){
                throw new NotFoundException("Не найден автор по идентификатору: " + authorId);
            }
        }
        if(genreId > 0){
            genre = genreRepository.findById(genreId).orElse(null);
            if(genre == null){
                throw new NotFoundException("Не найден жанр по идентификатору: " + genreId);
            }
        }
        return new Book(bookDTO.getName(), author, genre, bookDTO.getDescription());
    }

    private static void checkBookIdValid(long bookId){
        if(bookId <= 0){
            throw new BookValidationException("Некорректный идентификатор для поиска книги: " + bookId);
        }
    }

    private static String noBookMessage(long bookId){
        return "Не найдена книга по идентификатору: " + bookId;
    }
}
