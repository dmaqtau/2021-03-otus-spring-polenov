package ru.otus.spring.dao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class QuestionConfigImpl implements QuestionConfig {
    int qualifyAnswerQty;

    public QuestionConfigImpl(@Value("${answer.qualify.qty}") int qualifyAnswerQty){
        this.qualifyAnswerQty = qualifyAnswerQty;
    }
}
