package ru.otus.spring.integration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("generator")
public class GeneratorConfig {
    private int ship;
    private int iron;
    private int fuel;
    private int module;
}
