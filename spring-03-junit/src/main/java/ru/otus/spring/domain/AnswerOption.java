package ru.otus.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnswerOption {
    int index;
    String text;

    @Override
    public String toString() {
        return this.text;
    }
}
