package ru.otus.spring.service;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class LocaleServiceImpl implements LocaleService {

    private final String defaultLocale;
    private final MessageSource messageSource;

    public LocaleServiceImpl(MessageSource messageSource,
                             @Value("${locale.default}") String defaultLocale){
        this.messageSource = messageSource;
        this.defaultLocale = defaultLocale;
    }

    @Override
    public String getLocalizedString(String name, String... params){
        return messageSource.getMessage(
                name,
                params,
                Locale.forLanguageTag(getApplicationLocale())
        );
    }

    private String getApplicationLocale() {
        if(StringUtils.isBlank(defaultLocale)){
            return "";
        }
        return defaultLocale.split("_")[0];
    }
}
