package ru.otus.spring.service;

public interface QuestionLocaleService {
    String getWelcomeMessage();
    String getWrongAnswerInputMessage();
    String getWrongAnswerOptionMessage(int answerOptionCount);
    String getResultMessage(boolean isPassed, boolean isPerfect, int correctCount, int neededCount, int overallCount);
    String getApplicationLocale();
    String getAnswerErrorMessage();
    String getNoValidQuestionsMessage();
    String getEmptyQuestionsMessage();
    String getQuestionAskMessage();
    String getAboutMessage();
}
