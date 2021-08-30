package ru.otus.spring.exception;

public class BookValidationException extends InvalidInputException {
    public BookValidationException(String msg){
        super(msg);
    }
}
