package ru.otus.spring.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.otus.spring.domain.Question;
import ru.otus.spring.util.Const;

@Service
@RequiredArgsConstructor
public class QuestionValidationServiceImpl implements QuestionValidationService {
    private final LocaleService localeService;
    private final IOService ioService;

    @Override
    public Optional<List<Question>> validateQuestions(List<Question> questions){
        final String corruptMsg = localeService.getLocalizedString(Const.STRINGS_NO_VALID_QUESTIONS);
        final String emptyMsg = localeService.getLocalizedString(Const.STRINGS_EMPTY_MESSAGE_LIST);

        if (CollectionUtils.isEmpty(questions)) {
            ioService.out(emptyMsg);
            return Optional.empty();
        }

        List<Question> validQuestions = getValidQuestions(questions);

        if (CollectionUtils.isEmpty(validQuestions)) {
            ioService.out(corruptMsg);
            return Optional.empty();
        }
        return Optional.of(questions);
    }

    private static List<Question> getValidQuestions(List<Question> questions) {
        return questions.stream()
                .filter(Objects::nonNull)
                .filter(el -> StringUtils.isNotBlank(el.getQuestionText()) && !CollectionUtils.isEmpty(el.getAnswers()))
                .collect(Collectors.toList());
    }
}
