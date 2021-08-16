package ru.otus.spring.dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class QuestionDaoCSV implements QuestionDao {
    private final String resourceFile;
    private final QuestionConfig questionConfig;

    public QuestionDaoCSV(String resourceFile, QuestionConfig questionConfig) {
        this.resourceFile = resourceFile;
        this.questionConfig = questionConfig;
    }

    public List<Question> getQuestions() {
        final List<Question> questions = new ArrayList<>();
        URL url = QuestionDaoCSV.class.getClassLoader().getResource(resourceFile);

        if(url == null){
            System.out.println("No resource found by name: " + resourceFile);
            return questions;
        }

        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(questionConfig.getDelimiter());

        try (CSVParser csvFileParser = CSVParser.parse(url, UTF_8, csvFormat)) {
            List<CSVRecord> records = csvFileParser.getRecords();
            records.forEach(r -> questions.add(parseQuestion(r)));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return questions;
    }

    private Question parseQuestion(CSVRecord csvRecord) {
        Question question = new Question();

        question.setOrdinalNumber(csvRecord.getRecordNumber());
        question.setQuestionText(csvRecord.get(questionConfig.getQuestionColumnIdx()));
        question.setCorrectAnswerIndex(Integer.parseInt(csvRecord.get(questionConfig.getCorrectAnswerColumnIdx())));
        question.setAnswers(parseAnswers(csvRecord));
        return question;
    }

    private List<AnswerOption> parseAnswers(CSVRecord csvRecord) {
        List<AnswerOption> answers = new ArrayList<>(questionConfig.getMaxQuestionCount());

        for (int i = questionConfig.getFirstQuestionColumnIdx(); i <= questionConfig.getMaxQuestionCount(); i++) {
            try {
                answers.add(new AnswerOption(i, csvRecord.get(i)));
            } catch (ArrayIndexOutOfBoundsException e) {
                // Вопросы кончились
                break;
            }
        }
        return answers;
    }
}
