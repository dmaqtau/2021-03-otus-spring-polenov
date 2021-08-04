package ru.otus.spring.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.otus.spring.dao.QuestionConfig;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionDao questionDao;
    private final QuestionConfig questionConfig;
    private final IOService ioService;

    private static final int ERR_GIVEN_ANSWER_IDX = -1;

    public QuestionServiceImpl(QuestionDao questionDao,
                               QuestionConfig questionConfig,
                               IOService ioService) {
        this.questionDao = questionDao;
        this.questionConfig = questionConfig;
        this.ioService = ioService;
    }

    @Override
    public void startStudentExamination() {
        int correctCount = 0;
        int neededAnswers = questionConfig.getPassAnswerQty();

        List<Question> questions = questionDao.getQuestions();

        // Проверим, готовы ли вопросы для проведения экзамена
        List<Question> validQuestions = validateQuestions(questions).orElse(List.of());
        if(CollectionUtils.isEmpty(validQuestions)){
            // Вопросов нет. Экзамен не может состояться.
            return;
        }

        for (Question question : validQuestions.stream()
                .sorted(Comparator.comparingLong(Question::getOrdinalNumber))
                .collect(Collectors.toList())) {

            if (askSingleQuestion(question)) {
                correctCount++;
            }
        }

        boolean isPassed = correctCount >= neededAnswers;
        boolean isPerfect = isPassed && correctCount == validQuestions.size();

        ioService.out(
                formatFinishMessage(isPassed, isPerfect, correctCount, neededAnswers, questions.size())
        );
    }

    private Optional<List<Question>> validateQuestions(List<Question> questions){
        if (CollectionUtils.isEmpty(questions)) {
            ioService.out("Sorry, the questions list has not been prepared for the exam. Please come back later.");
            return Optional.empty();
        }

        List<Question> validQuestions = getValidQuestions(questions);

        if (CollectionUtils.isEmpty(validQuestions)) {
            ioService.out("Sorry, questions are corrupt. Please come back later.");
            return Optional.empty();
        }
        return Optional.of(questions);
    }

    private boolean askSingleQuestion(Question question) {
        final int answerOptionCount = question.getAnswers().size();

        GivenAnswer givenAnswer = new GivenAnswer(ERR_GIVEN_ANSWER_IDX, null);

        // Необходимо получить от пользователя корректный ответ и сопоставить его с индексом ответа на вопрос
        while (givenAnswer.getAnswerIdx() < 0) {
            printQuestion(question, StringUtils.isBlank(givenAnswer.getErrNotification()) ? "" : givenAnswer.getErrNotification() + "\n");

            try {
                givenAnswer = getUserAnswer(answerOptionCount);

                if(StringUtils.isBlank(givenAnswer.getErrNotification())){
                    // Формат ответа корректный. Проверим, правильный ли сам ответ.
                    return question.getCorrectAnswerIndex() == givenAnswer.getAnswerIdx();
                }

            } catch (Exception e) {
                ioService.out(String.format("Sorry, the given answer cannot be processed. Reason: %s", e.getMessage()));
                return false;
            }
        }
        return false;
    }

    private GivenAnswer getUserAnswer(int answerOptionCount) throws IOException {
        String givenAnswer = ioService.readLine();
        String errNotification;

        int answerIdx = getGivenAnswerPosition(givenAnswer) - 1;

        if (answerIdx < 0) {
            // Индекс ответа не получен, неверный ввод
            errNotification = "Wrong input. Please select one of: a, b, c,.... or 1, 2, 3, ....";
        } else if (answerIdx > answerOptionCount - 1) {
            answerIdx = ERR_GIVEN_ANSWER_IDX;
            errNotification = String.format("Wrong answer option. You have only %d of them.", answerOptionCount);
        } else {
            return new GivenAnswer(answerIdx, null);
        }
        return new GivenAnswer(answerIdx, errNotification);
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

    private static String formatFinishMessage(boolean isPassed,
                                              boolean isPerfect,
                                              int correctCount,
                                              int neededAnswers,
                                              int questionsSize){
        return String.format("You have %s%spassed the exam. Correct answers: %d. Needed: at least %d of %d.",
                (isPerfect? "perfectly ": ""),
                (isPassed ? "" : "NOT "),
                correctCount,
                neededAnswers,
                questionsSize);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class GivenAnswer {
        int answerIdx;
        String errNotification;
    }
}
