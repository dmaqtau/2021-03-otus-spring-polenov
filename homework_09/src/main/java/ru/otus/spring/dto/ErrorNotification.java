package ru.otus.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorNotification {
    private String status;
    private String message;
}
