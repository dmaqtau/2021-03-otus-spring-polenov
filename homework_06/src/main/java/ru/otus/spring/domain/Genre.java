package ru.otus.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Genre {
    private long id;
    private String genreName;

    @Override
    public String toString(){
        return genreName;
    }
}
