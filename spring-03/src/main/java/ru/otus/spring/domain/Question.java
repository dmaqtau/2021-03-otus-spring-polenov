package ru.otus.spring.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private long ordinalNumber;
    private String questionText;
    private List<AnswerOption> answers;
    private int correctAnswerIndex;
}
