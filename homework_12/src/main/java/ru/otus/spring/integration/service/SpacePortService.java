package ru.otus.spring.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.otus.spring.integration.domain.SpaceShip;

@Slf4j
@Service
public class SpacePortService {
    public static final String NAME = StringUtils.uncapitalize(SpacePortService.class.getSimpleName());
    public static final String ACCEPT_SHIP_METHOD = "acceptNewShip";

    public void acceptNewShip(SpaceShip spaceShip) throws InterruptedException {
        Thread.sleep(500L);     // Перегоняем корабль с завода
        log.info("Корабль ({}: {}) прибыл в космопорт.", spaceShip.getModel().getName(), spaceShip.getUuid());
    }
}
