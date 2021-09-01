package ru.otus.spring.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.otus.spring.domain.Author;
import ru.otus.spring.domain.Book;
import ru.otus.spring.domain.Genre;
import ru.otus.spring.dto.BookCommentDTO;
import ru.otus.spring.dto.BookDTO;
import ru.otus.spring.exception.AuthorValidationException;
import ru.otus.spring.exception.BookValidationException;
import ru.otus.spring.exception.CommentValidationException;
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

    void validateBookForUpdate(BookDTO book){
        if(book == null){
            throw new BookValidationException("Передан пустой объект книги.");
        }

        if(book.getId() <= 0){
            throw new BookValidationException("Передан некорректный идентификатор книги.");
        }

        if(StringUtils.isBlank(book.getName())){
            throw new BookValidationException("Должно быть передано наименование книги.");
        }
    }

    void validateBookComment(BookCommentDTO comment){
        if(comment == null){
            throw new CommentValidationException("Передан пустой объект комментария.");
        }
        if(StringUtils.isBlank(comment.getUserLogin())){
            throw new CommentValidationException("Не передан логин пользователя.");
        }
        if(StringUtils.isBlank(comment.getComment())){
            throw new CommentValidationException("Не передан текст комментария.");
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
