package ru.otus.spring.service;

public interface LocaleService {
    String getLocalizedString(String name, String... params);
}
