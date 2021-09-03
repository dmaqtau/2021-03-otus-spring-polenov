package ru.otus.spring.integration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("warehouse.initialstock")
public class InitialStockConfig {
    private int iron;
    private int module;
    private int fuel;
}
