package ru.otus.spring.exception;

public class CommentValidationException extends RuntimeException{
    public CommentValidationException(String msg){
        super(msg);
    }
}
