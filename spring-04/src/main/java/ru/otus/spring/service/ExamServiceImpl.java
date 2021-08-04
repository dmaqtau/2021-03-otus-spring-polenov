package ru.otus.spring.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.otus.spring.dao.QuestionConfig;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;
import ru.otus.spring.util.Const;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final QuestionDao questionDao;
    private final QuestionConfig questionConfig;
    private final LocaleService localeService;
    private final IOService ioService;
    private final QuestionValidationService questionValidationService;
    private final AnswerReaderService answerReaderService;

    @Override
    public void startStudentExamination() {
        int correctCount = 0;
        final int neededAnswers = questionConfig.getQualifyAnswerQty();

        final List<Question> questions = questionDao.getQuestions();

        // Проверим, готовы ли вопросы для проведения экзамена
        List<Question> validQuestions = questionValidationService.validateQuestions(questions).orElse(List.of());
        if(CollectionUtils.isEmpty(validQuestions)){
            // Вопросов нет. Экзамен не может состояться.
            return;
        }

        printWelcome();

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
                getResultMessage(isPassed, isPerfect, correctCount, neededAnswers, questions.size())
        );
    }

    private boolean askSingleQuestion(Question question) {
        final int answerOptionCount = question.getAnswers().size();

        GivenAnswer givenAnswer = new GivenAnswer(Const.ERR_GIVEN_ANSWER_IDX, null);

        // Необходимо получить от пользователя корректный ответ и сопоставить его с индексом ответа на вопрос
        while (givenAnswer.getAnswerIdx() < 0) {
            printQuestion(question, StringUtils.isBlank(givenAnswer.getErrNotification()) ? "" : givenAnswer.getErrNotification() + "\n");

            try {
                givenAnswer = answerReaderService.getUserAnswer(answerOptionCount);

                if(StringUtils.isBlank(givenAnswer.getErrNotification())){
                    // Формат ответа корректный. Проверим, правильный ли сам ответ.
                    return question.getCorrectAnswerIndex() == givenAnswer.getAnswerIdx();
                }

            } catch (Exception e) {
                ioService.out(localeService.getLocalizedString(Const.STRINGS_ANSWER_ERROR));
                return false;
            }
        }
        return false;
    }

    private void printQuestion(Question question, String prefix) {
        String questionMessage = localeService.getLocalizedString(Const.STRINGS_QUESION_ASK);

        ioService.out(
                String.format("%s%n%s%n",
                        String.format("%s%s%d. %s", prefix, questionMessage, question.getOrdinalNumber(), question.getQuestionText()),
                        question.getAnswers().stream().map(AnswerOption::toString).collect(Collectors.joining("; ")))
        );
    }

    private void printWelcome(){
        ioService.out(String.format("%n%n%s%n", localeService.getLocalizedString(Const.STRINGS_WELCOME)));
    }

    private String getResultMessage(boolean isPassed, boolean isPerfect, int correctCount, int neededCount, int overallCount) {
        String passedMessage = isPassed ? "": localeService.getLocalizedString(Const.STRINGS_FAILED);
        String perfectMessage = isPerfect ? localeService.getLocalizedString(Const.STRINGS_PERFECT): "";
        return localeService.getLocalizedString(
                Const.STRINGS_RESULT, perfectMessage, passedMessage,
                        String.valueOf(correctCount),
                        String.valueOf(neededCount),
                        String.valueOf(overallCount)
                )
                .replaceAll("\\s{2,}", " ")
                .trim();
    }
}
