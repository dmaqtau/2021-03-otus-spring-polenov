package ru.otus.spring.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import ru.otus.spring.dao.QuestionConfig;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;
import ru.otus.spring.exception.QuestionException;

public class QuestionServiceImpl implements QuestionService {
    private final QuestionDao questionDao;
    private final QuestionConfig questionConfig;
    private final IOService ioService;

    private static final String NO_VALID_QUESTIONS = "No valid questions available.";
    private static final int ERR_GIVEN_ANSWER_IDX = -1;

    public QuestionServiceImpl(QuestionDao questionDao,
                               QuestionConfig questionConfig,
                               IOService ioService) {
        this.questionDao = questionDao;
        this.questionConfig = questionConfig;
        this.ioService = ioService;
    }

    @Override
    public void startTest() {
        int correctCount = 0;
        int neededAnswers = questionConfig.getPassAnswerQty();

        List<Question> questions = questionDao.getQuestions();
        if (CollectionUtils.isEmpty(questions)) {
            throw new QuestionException(NO_VALID_QUESTIONS);
        }

        List<Question> validQuestions = getValidQuestions(questions);

        if (CollectionUtils.isEmpty(validQuestions)) {
            throw new QuestionException(NO_VALID_QUESTIONS);
        }

        for (Question question : validQuestions.stream()
                .sorted(Comparator.comparingLong(Question::getOrdinalNumber))
                .collect(Collectors.toList())) {

            if (testSingleQuestion(question)) {
                correctCount++;
            }
        }

        boolean isPassed = correctCount >= neededAnswers;
        boolean isPerfect = isPassed && correctCount == validQuestions.size();

        ioService.out(
                String.format("You have %s%spassed the exam. Correct answers: %d. Needed: at least %d of %d.",
                        (isPerfect? "perfectly ": ""),
                        (isPassed ? "" : "NOT "),
                        correctCount,
                        neededAnswers,
                        questions.size())
        );
    }

    private boolean testSingleQuestion(Question question) {
        String givenAnswer;
        String errNotification = null;
        int answerIdx = ERR_GIVEN_ANSWER_IDX;
        int answerOptionCount = question.getAnswers().size();

        // Необходимо получить от пользователя корректный ответ и сопоставить его с индексом ответа на вопрос
        while (answerIdx < 0) {
            printQuestion(question, StringUtils.isBlank(errNotification) ? "" : errNotification + "\n");

            try {
                givenAnswer = ioService.readLine();
                answerIdx = getGivenAnswerPosition(givenAnswer) - 1;

                if (answerIdx < 0) {
                    // Индекс ответа не получен, неверный ввод
                    errNotification = "Wrong input. Please select one of: a, b, c,.... or 1, 2, 3, ....";
                } else if (answerIdx > answerOptionCount - 1) {
                    answerIdx = ERR_GIVEN_ANSWER_IDX;
                    errNotification = String.format("Wrong answer option. You have only %d of them.", answerOptionCount);
                } else {
                    return question.getCorrectAnswerIndex() == answerIdx;
                }
            } catch (Exception e) {
                throw new QuestionException("Error while getting user answer.", e);
            }
        }
        return false;
    }

    private void printQuestion(Question question, String prefix) {
        ioService.out(
                String.format("%s%n%s%n",
                        String.format("%sQuestion №%d. %s", prefix, question.getOrdinalNumber(), question.getQuestionText()),
                        question.getAnswers().stream().map(AnswerOption::toString).collect(Collectors.joining("; ")))
        );
    }

    private static List<Question> getValidQuestions(List<Question> questions) {
        return questions.stream()
                .filter(Objects::nonNull)
                .filter(el -> StringUtils.isNotBlank(el.getQuestionText()) && !CollectionUtils.isEmpty(el.getAnswers()))
                .collect(Collectors.toList());
    }

    private static int getGivenAnswerPosition(String userInput) {
        if (StringUtils.isBlank(userInput) || userInput.length() > 1) {
            return ERR_GIVEN_ANSWER_IDX;
        }
        if (StringUtils.isNumeric(userInput)) {
            int inputInt = Integer.parseInt(userInput);
            return inputInt > 0 ? inputInt : ERR_GIVEN_ANSWER_IDX;
        }

        char c = userInput.charAt(0);
        if (isLatinLetter(c)) {
            return getLetterOrdinalNumber(c);
        }
        return ERR_GIVEN_ANSWER_IDX;
    }

    private static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private static int getLetterOrdinalNumber(char c) {
        return Character.toUpperCase(c) - 64;
    }
}
