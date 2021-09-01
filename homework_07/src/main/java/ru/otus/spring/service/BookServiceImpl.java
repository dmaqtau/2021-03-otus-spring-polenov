package ru.otus.spring.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.otus.spring.dao.AuthorRepository;
import ru.otus.spring.dao.BookCommentRepository;
import ru.otus.spring.dao.BookRepository;
import ru.otus.spring.dao.GenreRepository;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.BookComment;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.exception.AuthorValidationException;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.exception.CommentValidationException;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookCommentRepository commentRepository;

    private final LibraryObjectValidator validator;

    @Override
    @Transactional
    public Book create(String bookName, String bookDescription, long authorId, long genreId) {
        Book book = getBookForSave(bookName, bookDescription, authorId, genreId);

        validator.validateBook(book);
        validator.validateAuthor(book.getAuthor());
        validator.validateGenre(book.getGenre());
        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Book findByID(long id) {
        if(id <= 0){
            throw new IllegalArgumentException("Некорректный идентификатор для поиска книги: " + id);
        }
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book update(long id, String bookName, String bookDescription, long authorId, long genreId) {
        Book book = getBookForUpdate(id, bookName, bookDescription, authorId, genreId);
        validator.validateBookForUpdate(book);

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
        if(id <= 0){
            throw new IllegalArgumentException("Некорректный идентификатор для поиска книги: " + id);
        }
        checkBookExists(id);
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookComment> findComments(long bookId) {
        checkBookExists(bookId);
        return commentRepository.findByBookId(bookId);
    }

    @Override
    @Transactional
    public BookComment addComment(long bookId, String userLogin, String commentValue) {
        checkBookExists(bookId);
        return commentRepository.save(userLogin, commentValue, bookId);
    }

    @Override
    @Transactional
    public void deleteCommentById(long commentId) {
        if(!commentRepository.isExistsById(commentId)){
            throw new CommentValidationException("Не найден комментарий по идентификатору: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByBookId(long bookId) {
        checkBookExists(bookId);

        List<BookComment> comments = commentRepository.findByBookId(bookId);

        if(CollectionUtils.isEmpty(comments)){
            throw new CommentValidationException("У книги с идентификатором " + bookId + " нет комментариев.");
        }
        commentRepository.deleteByBookId(bookId);
    }

    private void checkBookExists(long bookId){
        if(!bookRepository.isExistsById(bookId)){
            throw new BookValidationException(getNoBookFoundMessage(bookId));
        }
    }

    private Book getBookForUpdate(long bookId, String bookName, String bookDescription, long authorId, long genreId){
        Book existingBook = bookRepository.findById(bookId);
        if(existingBook == null){
            throw new BookValidationException(getNoBookFoundMessage(bookId));
        }

        if(authorId > 0){
            Author author = authorRepository.findById(authorId);
            if(author == null){
                throw new AuthorValidationException("Не найден автор по идентификатору: " + authorId);
            }
            existingBook.setAuthor(author);
        }
        if(genreId > 0){
            Genre genre = genreRepository.findById(genreId);
            if(genre == null){
                throw new AuthorValidationException("Не найден жанр по идентификатору: " + genreId);
            }
            existingBook.setGenre(genre);
        }
        existingBook.setName(bookName);
        existingBook.setDescription(bookDescription);
        return existingBook;
    }

    private Book getBookForSave(String bookName, String bookDescription, long authorId, long genreId){
        Author author = null;
        Genre genre = null;

        if(authorId > 0){
            author = authorRepository.findById(authorId);
            if(author == null){
                throw new AuthorValidationException("Не найден автор по идентификатору: " + authorId);
            }
        }
        if(genreId > 0){
            genre = genreRepository.findById(genreId);
            if(genre == null){
                throw new AuthorValidationException("Не найден жанр по идентификатору: " + genreId);
            }
        }
        return new Book(bookName, author, genre, bookDescription);
    }
    
    private static String getNoBookFoundMessage(long bookId){
        return "Не найдена книга по идентификатору: " + bookId;
    }
}
