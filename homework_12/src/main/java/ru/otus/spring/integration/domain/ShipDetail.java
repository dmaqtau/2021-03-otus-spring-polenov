package ru.otus.spring.integration.domain;

import java.util.List;

import lombok.Getter;
import org.apache.commons.lang3.RandomUtils;

@Getter
public enum ShipDetail {
    IRON_BAR("Железный слиток"),
    MICROMODULE("Микромодуль"),
    FUEL_CELL("Топливный элемент");

    String name;

    ShipDetail(String name){
        this.name = name;
    }

    public static ShipDetail getRandom(){
        ShipDetail[] values = values();
        return List.of(values).get(RandomUtils.nextInt(0, values.length));
    }
}
