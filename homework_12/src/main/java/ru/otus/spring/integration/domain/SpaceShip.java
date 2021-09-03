package ru.otus.spring.integration.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpaceShip {
    private UUID uuid;
    private ShipModel model;
}

