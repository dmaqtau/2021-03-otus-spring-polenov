package ru.otus.spring.exception;

public class GenreValidationException extends RuntimeException {
    public GenreValidationException(String msg){
        super(msg);
    }
}
