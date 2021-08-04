package ru.otus.spring.service;

import org.springframework.stereotype.Service;
import ru.otus.spring.dao.BookDao;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookDao bookDao;
    private final LibraryObjectValidator validator;

    public BookServiceImpl(BookDao bookDao, LibraryObjectValidator validator){
        this.bookDao = bookDao;
        this.validator = validator;
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

        validator.validateBook(book);
        validator.validateAuthor(book.getAuthor());
        validator.validateGenre(book.getGenre());
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
                .author(authorId == 0L? null: new Author(authorId))
                .genre(genreId == 0L? null: new Genre(genreId))
                .build();
        validator.validateBookForUpdate(book);

        if(book.getAuthor() != null){
            validator.validateAuthor(book.getAuthor());
        }

        if(book.getGenre() != null){
            validator.validateGenre(book.getGenre());
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
