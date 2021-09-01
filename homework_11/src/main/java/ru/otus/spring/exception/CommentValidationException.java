package ru.otus.spring.exception;

public class CommentValidationException extends InvalidInputException{
    public CommentValidationException(String msg){
        super(msg);
    }
}
