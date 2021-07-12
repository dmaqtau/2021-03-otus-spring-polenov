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

    @Override
    public String toString(){
        if(patronymic == null){
            return String.format("%s %s", surname, authorName);
        }
        return String.format("%s %s %s", surname, authorName, patronymic);
    }
}
