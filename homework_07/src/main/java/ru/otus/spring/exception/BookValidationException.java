package ru.otus.spring.exception;

public class BookValidationException extends RuntimeException {
    public BookValidationException(String msg){
        super(msg);
    }
}
