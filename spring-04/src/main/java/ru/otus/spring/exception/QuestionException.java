package ru.otus.spring.exception;

public class QuestionException extends RuntimeException {
    public QuestionException(String msg) {
        super("Failed to load questions: " + msg);
    }

    public QuestionException(String msg, Throwable cause) {
        super("Failed to load questions: " + msg, cause);
    }
}
