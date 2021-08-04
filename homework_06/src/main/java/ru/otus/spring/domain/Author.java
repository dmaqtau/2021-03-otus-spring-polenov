package ru.otus.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
@Builder
@AllArgsConstructor
public class Author {
    private long id;
    private String surname;
    private String authorName;
    private String patronymic;

    public Author(long id){
        this.id = id;
    }
}
