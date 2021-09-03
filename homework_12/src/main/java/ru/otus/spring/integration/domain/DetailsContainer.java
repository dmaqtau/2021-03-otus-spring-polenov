package ru.otus.spring.integration.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetailsContainer {
    private ShipDetail detail;
    private int qty;
}
