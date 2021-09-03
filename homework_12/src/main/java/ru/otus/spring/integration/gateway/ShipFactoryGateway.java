package ru.otus.spring.integration.gateway;


import java.util.Collection;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.spring.integration.domain.DetailsContainer;
import ru.otus.spring.integration.domain.SpaceShip;

@MessagingGateway
public interface ShipFactoryGateway {

    @Gateway(requestChannel = "productionChannel")
    Collection<SpaceShip> assembly(Collection<SpaceShip> shipsToProduce);

    @Gateway(requestChannel = "supplyChannel")
    void addStock(Collection<DetailsContainer> newStock);
}
