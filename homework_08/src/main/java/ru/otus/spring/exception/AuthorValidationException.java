package ru.otus.spring.exception;

public class AuthorValidationException extends RuntimeException {
    public AuthorValidationException(String msg){
        super(msg);
    }
}
