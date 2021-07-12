package ru.otus.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Book {
    private long id;
    private String bookName;
    private Author author;
    private Genre genre;
    private String description;

    @Override
    public String toString(){
        return String.format(
                "id = [%d],\nИмя = [%s],\nАвтор = [%s],\nЖанр = [%s],\nОписание = [%s]",
                id, bookName, author.toString(), genre.toString(), description
        );
    }
}
