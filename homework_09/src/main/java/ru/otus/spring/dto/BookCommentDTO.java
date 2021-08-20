package ru.otus.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// POJO для обмена данными о комментарии к книге
@Data
@AllArgsConstructor
public class BookCommentDTO {
    private Long bookId;
    private String userLogin;
    private String comment;
}
