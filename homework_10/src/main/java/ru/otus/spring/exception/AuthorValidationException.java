package ru.otus.spring.exception;

public class AuthorValidationException extends InvalidInputException {
    public AuthorValidationException(String msg){
        super(msg);
    }
}
