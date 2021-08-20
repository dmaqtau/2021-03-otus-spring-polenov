package ru.otus.spring.exception;

public class CSVParseException extends RuntimeException {
    public CSVParseException(String filePath) {
        super("Failed to parse CSV file: " + filePath);
    }

    public CSVParseException(String filePath, Throwable cause) {
        super("Failed to parse CSV file: " + filePath, cause);
    }
}
