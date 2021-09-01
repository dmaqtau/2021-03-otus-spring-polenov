package ru.otus.spring.exception;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(Throwable e){
        super(e);
    }
}
