package ru.otus.spring.dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ru.otus.spring.domain.AnswerOption;
import ru.otus.spring.domain.Question;
import ru.otus.spring.exception.CSVParseException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class QuestionDaoCSV implements QuestionDao {
    private final ParseConfig parseConfig;

    public QuestionDaoCSV(ParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }

    public List<Question> getQuestions() {
        String resourceFilePath = parseConfig.getQuestionFilePath();

        if (resourceFilePath == null) {
            throw new CSVParseException("Please set resource file path value.");
        }

        final List<Question> parsedQuestions = new ArrayList<>();
        URL url = QuestionDaoCSV.class.getClassLoader().getResource(resourceFilePath);

        if (url == null) {
            throw new CSVParseException(resourceFilePath);
        }

        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(parseConfig.getDelimiter());

        try (CSVParser csvFileParser = CSVParser.parse(url, UTF_8, csvFormat)) {
            List<CSVRecord> records = csvFileParser.getRecords();
            records.forEach(r -> parsedQuestions.add(parseQuestion(r)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.shuffle(parsedQuestions);
        List<Question> questions = limitQuestions(parsedQuestions, parseConfig.getQuestionAskQty());

        setOrdinalNumbers(questions);
        return questions;
    }

    private Question parseQuestion(CSVRecord csvRecord) {
        Question question = new Question();

        question.setQuestionText(csvRecord.get(parseConfig.getQuestionColumnIdx()));
        question.setCorrectAnswerIndex(Integer.parseInt(csvRecord.get(parseConfig.getCorrectAnswerColumnIdx())));
        question.setAnswers(parseAnswers(csvRecord));
        return question;
    }

    private List<Question> limitQuestions(List<Question> questions, int qty) {
        if (questions.size() <= qty) {
            return questions;
        }
        return questions.stream().limit(qty).collect(Collectors.toList());
    }

    private void setOrdinalNumbers(List<Question> questions) {
        int i = 1;

        for (Question question : questions) {
            question.setOrdinalNumber(i);
            i++;
        }
    }

    private List<AnswerOption> parseAnswers(CSVRecord csvRecord) {
        int answerNumber = 0;
        int columnIdx = 0;

        List<AnswerOption> answers = new ArrayList<>();

        for (String answer : csvRecord) {
            if (columnIdx >= parseConfig.getFirstQuestionColumnIdx()) {
                answers.add(new AnswerOption(answerNumber, answer));
                answerNumber++;
            }

            columnIdx++;
        }
        return answers;
    }
}
