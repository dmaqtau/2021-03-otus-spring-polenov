package ru.otus.spring.service;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.otus.spring.util.Const;

@Service
@RequiredArgsConstructor
public class AnswerReaderServiceImpl implements AnswerReaderService {
    private final LocaleService  localeService;
    private final IOService ioService;

    @Override
    public GivenAnswer getUserAnswer(int answerOptionCount) throws IOException {
        String givenAnswer = ioService.readLine();
        String errNotification;

        int answerIdx = getGivenAnswerPosition(givenAnswer) - 1;

        if (answerIdx < 0) {
            // Индекс ответа не получен, неверный ввод
            errNotification = localeService.getLocalizedString(Const.STRINGS_WRONG_ANSWER_INPUT);
        } else if (answerIdx > answerOptionCount - 1) {
            answerIdx = Const.ERR_GIVEN_ANSWER_IDX;
            errNotification = localeService.getLocalizedString(
                    Const.STRINGS_WRONG_ANSWER_OPTION, String.valueOf(answerOptionCount)
            );
        } else {
            return new GivenAnswer(answerIdx, null);
        }
        return new GivenAnswer(answerIdx, errNotification);
    }

    private static int getGivenAnswerPosition(String userInput) {
        if (StringUtils.isBlank(userInput) || userInput.length() > 1) {
            return Const.ERR_GIVEN_ANSWER_IDX;
        }
        if (StringUtils.isNumeric(userInput)) {
            int inputInt = Integer.parseInt(userInput);
            return inputInt > 0 ? inputInt : Const.ERR_GIVEN_ANSWER_IDX;
        }

        char c = userInput.charAt(0);
        if (isLatinLetter(c)) {
            return getLetterOrdinalNumber(c);
        }
        return Const.ERR_GIVEN_ANSWER_IDX;
    }

    private static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private static int getLetterOrdinalNumber(char c) {
        return Character.toUpperCase(c) - 64;
    }
}
