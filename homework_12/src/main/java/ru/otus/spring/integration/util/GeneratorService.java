package ru.otus.spring.integration.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import ru.otus.spring.integration.config.GeneratorConfig;
import ru.otus.spring.integration.domain.DetailsContainer;
import ru.otus.spring.integration.domain.ShipDetail;
import ru.otus.spring.integration.domain.ShipModel;
import ru.otus.spring.integration.domain.SpaceShip;

@Service
public class GeneratorService {
    private final GeneratorConfig generatorConfig;

    public GeneratorService(GeneratorConfig generatorConfig){
        this.generatorConfig = generatorConfig;
    }

    public List<SpaceShip> getShipsToProduce(){
        List<SpaceShip> shipsToProduce = new ArrayList<>();

        int qty = RandomUtils.nextInt(1, generatorConfig.getShip() + 1);

        for(int i = 0; i < qty; i++){
            shipsToProduce.add(new SpaceShip(null, ShipModel.getRandom()));
        }
        return shipsToProduce.stream()
                .sorted(Comparator.comparing(s -> s.getModel().getName()))
                .collect(Collectors.toList());
    }

    public List<DetailsContainer> getNewStock(){
        DetailsContainer ironContainer = new DetailsContainer(ShipDetail.IRON_BAR, RandomUtils.nextInt(1, generatorConfig.getIron() + 1));
        DetailsContainer moduleContainer = new DetailsContainer(ShipDetail.MICROMODULE, RandomUtils.nextInt(1, generatorConfig.getModule() + 1));
        DetailsContainer fuelContainer = new DetailsContainer(ShipDetail.FUEL_CELL, RandomUtils.nextInt(1, generatorConfig.getFuel() + 1));

        return List.of(ironContainer, moduleContainer, fuelContainer);
    }
}
