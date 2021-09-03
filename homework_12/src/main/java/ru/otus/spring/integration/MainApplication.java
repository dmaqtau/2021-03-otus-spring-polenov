package ru.otus.spring.integration;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import ru.otus.spring.integration.domain.DetailsContainer;
import ru.otus.spring.integration.domain.SpaceShip;
import ru.otus.spring.integration.gateway.ShipFactoryGateway;
import ru.otus.spring.integration.util.GeneratorService;


@Slf4j
@IntegrationComponentScan
@Configuration
@EnableIntegration
@EnableConfigurationProperties
@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(MainApplication.class, args);

        ForkJoinPool pool = ForkJoinPool.commonPool();

        ShipFactoryGateway shipFactoryGateway = context.getBean(ShipFactoryGateway.class);
        GeneratorService generatorService = context.getBean(GeneratorService.class);

        pool.execute(
                () -> {
                    try {
                        startConveyor(shipFactoryGateway, generatorService);
                    } catch (Exception e) {
                        log.error("Производство прервано из-за непредвиденной ошибки:", e);
                    }
                }
        );

        pool.execute(
                () -> {
                    try {
                        startSupply(shipFactoryGateway, generatorService);
                    } catch (Exception e) {
                        log.error("Поступление деталей прервано из-за непредвиденной ошибки:", e);
                    }
                }
        );
    }

    private static void startConveyor(ShipFactoryGateway shipFactoryGateway, GeneratorService generatorService) throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
            List<SpaceShip> shipsToProduce = generatorService.getShipsToProduce();

            log.info("Задание на постройку кораблей: [{}]", shipsToProduce.stream().map(f -> f.getModel().getName()).collect(Collectors.joining(", ")));
            Collection<SpaceShip> producedShips = shipFactoryGateway.assembly(shipsToProduce);

            String newShipsDescription = producedShips.stream()
                    .filter(ship -> ship != null && ship.getUuid() != null)
                    .map(f -> f.getModel().getName())
                    .collect(Collectors.joining(", "));

            log.info("Готовые корабли: {}", StringUtils.isBlank(newShipsDescription) ? "<ничего не произведено>": newShipsDescription);
        }
    }

    private static void startSupply(ShipFactoryGateway shipFactoryGateway, GeneratorService generatorService) throws InterruptedException{
        while (true) {
            Thread.sleep(2000);
            List<DetailsContainer> newStock = generatorService.getNewStock();
            shipFactoryGateway.addStock(newStock);
        }
    }
}
