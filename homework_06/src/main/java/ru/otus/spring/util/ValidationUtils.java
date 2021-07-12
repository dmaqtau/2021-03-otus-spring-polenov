package ru.otus.spring.util;

import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.exception.AuthorValidationException;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.exception.GenreValidationException;

public class ValidationUtils {
    public static void validateBook(Book book){
        if(book == null){
            throw new BookValidationException("Передан пустой объект книги.");
        }

        if(book.getBookName() == null){
            throw new BookValidationException("Не задано имя книги.");
        }

        if(book.getAuthor() == null ){
            throw new BookValidationException("Не задан автор книги.");
        }

        if(book.getGenre() == null){
            throw new BookValidationException("Не задан жанр книги.");
        }
    }

    public static void validateBookForUpdate(Book book){
        if(book == null){
            throw new BookValidationException("Передан пустой объект книги.");
        }

        if(book.getId() <= 0){
            throw new BookValidationException("Передан некорректный идентификатор книги.");
        }
    }

    public static void validateAuthor(Author author){
        if(author.getId() <=0){
            throw new AuthorValidationException("Некорректный идентификатор автора: " + author.getId());
        }
    }

    public static void validateGenre(Genre genre){
        if(genre.getId() <=0){
            throw new GenreValidationException("Некорректный идентификатор жанра: " + genre.getId());
        }
    }
}
