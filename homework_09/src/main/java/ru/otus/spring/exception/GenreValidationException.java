package ru.otus.spring.exception;

public class GenreValidationException extends InvalidInputException {
    public GenreValidationException(String msg){
        super(msg);
    }
}
