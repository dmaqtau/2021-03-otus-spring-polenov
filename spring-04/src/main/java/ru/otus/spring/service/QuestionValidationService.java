package ru.otus.spring.service;

import java.util.List;
import java.util.Optional;

import ru.otus.spring.domain.Question;

public interface QuestionValidationService {
    Optional<List<Question>> validateQuestions(List<Question> questions);
}
