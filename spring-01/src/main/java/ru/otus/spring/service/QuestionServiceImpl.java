package ru.otus.spring.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;

public class QuestionServiceImpl implements QuestionService {
    private final QuestionDao questionDao;

    public QuestionServiceImpl(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public void testStudent() {
        List<Question> questions = questionDao.getQuestions();
        if(CollectionUtils.isEmpty(questions)){
            System.out.println("Failed to load questions. Testing is cancelled.");
            return;
        }

        questions.stream()
                .sorted(Comparator.comparingLong(Question::getOrdinalNumber))
                .forEach(
                        q -> System.out.println(
                                String.format("%s\n%s\n",
                                        String.format("Question №%d. ", q.getOrdinalNumber()) + Optional.of(q.getQuestionText()).orElse("<no question text>"),
                                        Optional.of(q.getAnswers().stream().map(AnswerOption::toString).collect(Collectors.joining("; "))).orElse("<no answers>"))
                        )
                );
    }
}
