package ru.otus.spring.integration.domain;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import org.apache.commons.lang3.RandomUtils;

@Getter
public enum ShipModel {
    MINING_DRONE("Добывающий дрон", Map.of(ShipDetail.IRON_BAR, 1, ShipDetail.MICROMODULE, 2, ShipDetail.FUEL_CELL, 1)),
    DESTROYER("Эсминец", Map.of(ShipDetail.IRON_BAR, 3, ShipDetail.MICROMODULE, 3, ShipDetail.FUEL_CELL, 2)),
    TRANSPORT("Транспортный корабль", Map.of(ShipDetail.IRON_BAR, 2, ShipDetail.MICROMODULE, 3, ShipDetail.FUEL_CELL, 2)),
    BATTLECRUISER("Крейсер", Map.of(ShipDetail.IRON_BAR, 5, ShipDetail.MICROMODULE, 5, ShipDetail.FUEL_CELL, 7)),
    UBERSHIP("Пепелац", Map.of(ShipDetail.IRON_BAR, 7, ShipDetail.MICROMODULE, 1, ShipDetail.FUEL_CELL, 1));

    String name;
    Map<ShipDetail, Integer> requiredDetails;

    ShipModel(String name, Map<ShipDetail, Integer> requiredDetails){
        this.name = name;
        this.requiredDetails = requiredDetails;
    }

    public static ShipModel getRandom(){
        ShipModel[] values = values();
        return List.of(values).get(RandomUtils.nextInt(0, values.length));
    }
}
