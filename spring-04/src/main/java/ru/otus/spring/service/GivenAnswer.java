package ru.otus.spring.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
class GivenAnswer {
    int answerIdx;
    String errNotification;
}
