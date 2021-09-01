package ru.otus.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// POJO для обмена данными о книге
@Data
@AllArgsConstructor
public class BookDTO {
    Long id;
    String name;
    String description;
    Long authorId;
    Long genreId;
}
