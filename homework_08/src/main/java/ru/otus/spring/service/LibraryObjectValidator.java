package ru.otus.spring.service;

import org.springframework.stereotype.Component;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.exception.AuthorValidationException;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.exception.GenreValidationException;

@Component
public class LibraryObjectValidator {
    void validateBook(Book book){
        if(book.getName() == null){
            throw new BookValidationException("Не задано имя книги.");
        }

        if(book.getAuthor() == null ){
            throw new BookValidationException("Не задан автор книги.");
        }

        if(book.getGenre() == null){
            throw new BookValidationException("Не задан жанр книги.");
        }
    }

    void validateBookForUpdate(Book book){
        if(book == null){
            throw new BookValidationException("Передан пустой объект книги.");
        }

        if(book.getId() <= 0){
            throw new BookValidationException("Передан некорректный идентификатор книги.");
        }

        if(book.getName() == null || book.getName().isBlank()){
            throw new BookValidationException("Должно быть передано наименование книги.");
        }
    }

    void validateAuthor(Author author){
        if(author.getId() <=0){
            throw new AuthorValidationException("Некорректный идентификатор автора: " + author.getId());
        }
    }

    void validateGenre(Genre genre){
        if(genre.getId() <=0){
            throw new GenreValidationException("Некорректный идентификатор жанра: " + genre.getId());
        }
    }
}
