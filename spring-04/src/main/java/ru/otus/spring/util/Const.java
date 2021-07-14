package ru.otus.spring.util;

public abstract class Const {
    private Const(){
        // Закрыли нештатное конструирование
    }

    public static final String STRINGS_WELCOME = "strings.welcome";
    public static final String STRINGS_RESULT = "strings.result";
    public static final String STRINGS_WRONG_ANSWER_INPUT = "strings.answer.input.wrong";
    public static final String STRINGS_WRONG_ANSWER_OPTION = "strings.answer.option.wrong";
    public static final String STRINGS_ANSWER_ERROR = "strings.answer.error";
    public static final String STRINGS_NO_VALID_QUESTIONS = "strings.questions.novalid";
    public static final String STRINGS_EMPTY_MESSAGE_LIST = "strings.questions.empty";
    public static final String STRINGS_QUESION_ASK = "strings.question.ask";
    public static final String STRINGS_ABOUT = "strings.about";

    public static final String STRINGS_FAILED = "strings.result.failed";
    public static final String STRINGS_PERFECT = "strings.result.perfect";

    public static final int ERR_GIVEN_ANSWER_IDX = -1;
}
