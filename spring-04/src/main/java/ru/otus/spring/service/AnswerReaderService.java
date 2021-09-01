package ru.otus.spring.service;

import java.io.IOException;

public interface AnswerReaderService {
    GivenAnswer getUserAnswer(int answerOptionCount) throws IOException;
}
