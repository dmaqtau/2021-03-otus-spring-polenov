package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import ru.otus.spring.dao.BookDao;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.util.ValidationUtils;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookDao bookDao;

    public BookServiceImpl(BookDao bookDao){
        this.bookDao = bookDao;
    }

    @Override
    public Book create(String bookName, String bookDescription, long authorId, long genreId) {
        Book book = Book.builder()
                .bookName(bookName)
                .description(bookDescription)
                .author(Author.builder()
                                .id(authorId)
                                .build()
                ).genre(Genre.builder()
                                .id(genreId)
                                .build())
                .build();

        ValidationUtils.validateBook(book);
        ValidationUtils.validateAuthor(book.getAuthor());
        ValidationUtils.validateGenre(book.getGenre());
        return bookDao.insert(book);
    }

    @Override
    public Book getByID(long id) {
        if(id <= 0){
            throw new IllegalArgumentException("Некорректный идентификатор для поиска книги: " + id);
        }
        return bookDao.getById(id);
    }

    @Override
    public List<Book> getAll() {
        return bookDao.getAll();
    }

    @Override
    public Book update(long id, String bookName, String bookDescription, long authorId, long genreId) {
        Book book = Book.builder()
                .id(id)
                .bookName(bookName)
                .description(bookDescription)
                .author(authorId == 0L? null:
                        Author.builder()
                                .id(authorId)
                                .build()
                ).genre(genreId == 0L? null:
                        Genre.builder()
                                .id(genreId)
                                .build())
                .build();
        ValidationUtils.validateBookForUpdate(book);

        if(book.getAuthor() != null){
            ValidationUtils.validateAuthor(book.getAuthor());
        }

        if(book.getGenre() != null){
            ValidationUtils.validateGenre(book.getGenre());
        }
        return bookDao.update(book);
    }

    @Override
    public int deleteByID(long id) {
        if(id <= 0){
            throw new IllegalArgumentException("Некорректный идентификатор для поиска книги: " + id);
        }

        return bookDao.deleteById(id);
    }
}
