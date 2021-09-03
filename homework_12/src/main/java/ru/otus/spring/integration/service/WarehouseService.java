package ru.otus.spring.integration.service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.spring.integration.config.InitialStockConfig;
import ru.otus.spring.integration.domain.ShipDetail;
import ru.otus.spring.integration.domain.DetailsContainer;
import ru.otus.spring.integration.domain.WarehouseStock;

@Slf4j
@Service
public class WarehouseService {
    private final WarehouseStock warehouseStock;

    public static final String NAME = StringUtils.uncapitalize(WarehouseService.class.getSimpleName());
    public static final String ADD_STOCK_METHOD = "addStock";
    public static final String REPORT_THEFT_METHOD = "reportTheft";

    @Autowired
    public WarehouseService(InitialStockConfig initialStockConfig){
        this.warehouseStock = new WarehouseStock(
                Map.of(ShipDetail.IRON_BAR, initialStockConfig.getIron(),
                        ShipDetail.MICROMODULE, initialStockConfig.getModule(),
                        ShipDetail.FUEL_CELL, initialStockConfig.getFuel()));
    }

    public void addStock(DetailsContainer detailsContainer){
        warehouseStock.add(detailsContainer.getDetail(), detailsContainer.getQty());
    }

    public void reportTheft(DetailsContainer container){
        log.warn("Произошла кража деталей по пути до склада. Недополучено ({}) в кол-ве {} шт.", container.getDetail().getName(), container.getQty());
    }

    void grabDetails(Map<ShipDetail, Integer> stock){
        warehouseStock.subtract(stock);
    }
}
