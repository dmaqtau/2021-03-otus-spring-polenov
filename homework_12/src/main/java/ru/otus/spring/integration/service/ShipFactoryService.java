package ru.otus.spring.integration.service;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.spring.integration.domain.SpaceShip;
import ru.otus.spring.integration.exception.OutOfStockException;

@Slf4j
@Service
public class ShipFactoryService {
    private final WarehouseService warehouseService;

    private final int maxRetries;
    private final int retryWaitDuration;
    private final int assemblyDuration;

    public static final String NAME = StringUtils.uncapitalize(ShipFactoryService.class.getSimpleName());
    public static final String ASSEMBLY_METHOD = "assembly";
    public static final String REPORT_FAIL_METHOD = "reportFail";

    public ShipFactoryService(WarehouseService warehouseService,
                              @Value("${shipfactory.retry.maxcount}")
                              int maxRetries,
                              @Value("${shipfactory.retry.period}")
                              int retryWaitDuration,
                              @Value("${shipfactory.assembly.duration}")
                              int assemblyDuration){
        this.warehouseService = warehouseService;
        this.maxRetries = maxRetries;
        this.retryWaitDuration = retryWaitDuration;
        this.assemblyDuration = assemblyDuration;
    }

    public SpaceShip assembly(SpaceShip ordered) {
        log.info("Собираем корабль: {}", ordered.getModel().getName());
        SpaceShip newShip;

        int retryCount = 1;
        do{
            newShip = tryAssembly(ordered, retryCount);
            if(newShip == null){
                retryCount++;
            }
        } while(newShip == null && retryCount <= maxRetries);

        if(newShip == null){
            return ordered;
        }

        log.info("{} построен. uuid = {}", newShip.getModel().getName(), newShip.getUuid().toString());
        return newShip;
    }

    public void reportFail(SpaceShip ordered){
        log.error("Не удалось собрать корабль ({}).", ordered.getModel().getName());
    }

    private SpaceShip tryAssembly(SpaceShip ordered, int tryNumber) {
        try{
            warehouseService.grabDetails(ordered.getModel().getRequiredDetails());
            Thread.sleep(assemblyDuration);

            return new SpaceShip(UUID.randomUUID(), ordered.getModel());
        } catch (OutOfStockException e){
            log.warn("{}, попытка №{}: {}.", ordered.getModel().getName(), tryNumber, e.getLocalizedMessage().toLowerCase());
            waitForRetry();
        } catch (Exception e){
            log.error("Ошибка при построении корабля: ", e);
        }
        return null;
    }

    private void waitForRetry(){
        try{
            Thread.sleep(retryWaitDuration);
        } catch (InterruptedException e){
            log.warn("Ошибка ожидания следующей попытки: ", e);
        }
    }
}
