package ru.otus.spring.service;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class QuestionLocaleServiceImpl implements QuestionLocaleService {

    private final String defaultLocale;
    private final MessageSource messageSource;

    static final String STRINGS_WELCOME = "strings.welcome";
    static final String STRINGS_RESULT = "strings.result";
    static final String STRINGS_WRONG_ANSWER_INPUT = "strings.answer.input.wrong";
    static final String STRINGS_WRONG_ANSWER_OPTION = "strings.answer.option.wrong";
    static final String STRINGS_ANSWER_ERROR = "strings.answer.error";
    static final String STRINGS_NO_VALID_QUESTIONS = "strings.questions.novalid";
    static final String STRINGS_EMPTY_MESSAGE_LIST = "strings.questions.empty";
    static final String STRINGS_QUESION_ASK = "strings.question.ask";
    static final String STRINGS_ABOUT = "strings.about";

    private static final String STRINGS_FAILED = "strings.result.failed";
    private static final String STRINGS_PERFECT = "strings.result.perfect";

    public QuestionLocaleServiceImpl(MessageSource messageSource,
                                     @Value("${locale.default}") String defaultLocale){
        this.messageSource = messageSource;
        this.defaultLocale = defaultLocale;
    }

    @Override
    public String getWelcomeMessage() {
        return getLocalizedString(STRINGS_WELCOME);
    }

    @Override
    public String getWrongAnswerInputMessage() {
        return getLocalizedString(STRINGS_WRONG_ANSWER_INPUT);
    }

    @Override
    public String getWrongAnswerOptionMessage(int answerOptionCount) {
        return getLocalizedString(STRINGS_WRONG_ANSWER_OPTION, new String [] { String.valueOf(answerOptionCount)});
    }

    @Override
    public String getResultMessage(boolean isPassed, boolean isPerfect, int correctCount, int neededCount, int overallCount) {
        String passedMessage = isPassed ? "": getLocalizedString(STRINGS_FAILED);
        String perfectMessage = isPerfect ? getLocalizedString(STRINGS_PERFECT): "";
        return getLocalizedString(
                STRINGS_RESULT,
                new String [] {
                        perfectMessage,
                        passedMessage,
                        String.valueOf(correctCount),
                        String.valueOf(neededCount),
                        String.valueOf(overallCount)
                })
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    @Override
    public String getApplicationLocale() {
        if(StringUtils.isBlank(defaultLocale)){
            return "";
        }
        return defaultLocale.split("_")[0];
    }

    @Override
    public String getAnswerErrorMessage() {
        return getLocalizedString(STRINGS_ANSWER_ERROR);
    }

    @Override
    public String getNoValidQuestionsMessage() {
        return getLocalizedString(STRINGS_NO_VALID_QUESTIONS);
    }

    @Override
    public String getEmptyQuestionsMessage() {
        return getLocalizedString(STRINGS_EMPTY_MESSAGE_LIST);
    }

    @Override
    public String getQuestionAskMessage() {
        return getLocalizedString(STRINGS_QUESION_ASK);
    }

    @Override
    public String getAboutMessage() {
        return getLocalizedString(STRINGS_ABOUT);
    }

    private String getLocalizedString(String name){
        return getLocalizedString(name, new String[] {});
    }

    private String getLocalizedString(String name, String[] params){
        return messageSource.getMessage(
                name,
                params,
                Locale.forLanguageTag(getApplicationLocale())
        );
    }
}
