package ru.otus.spring.integration.exception;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message){
        super(message);
    }
}
