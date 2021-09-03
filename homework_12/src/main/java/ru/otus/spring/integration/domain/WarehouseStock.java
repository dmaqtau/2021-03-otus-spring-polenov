package ru.otus.spring.integration.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import ru.otus.spring.integration.exception.OutOfStockException;

@Slf4j
public class WarehouseStock {
    private Map<ShipDetail, Integer> stock = new HashMap<>();

    public WarehouseStock(Map<ShipDetail, Integer> initialStock){
        stock.putAll(initialStock);
    }

    public synchronized void add(ShipDetail detail, Integer qty){
        stock.merge(detail, qty, Integer::sum);
        log.info("Поступило на склад деталей вида {}: {} шт", detail.getName(), qty);
    }

    public synchronized void subtract(Map<ShipDetail, Integer> detailMap){
        Map<ShipDetail, Integer> newStockTmp = new HashMap<>();

        for(Map.Entry<ShipDetail, Integer> entry: detailMap.entrySet()){
            Integer qty = entry.getValue();
            Integer currentStock = stock.get(entry.getKey());

            if(currentStock == null || currentStock - qty < 0){
                throw new OutOfStockException(String.format("Недостаточно деталей (%s) на складе: требуется %d, имеется %d",
                        entry.getKey().getName(), qty,
                        currentStock == null? 0: currentStock));
            }
            newStockTmp.put(entry.getKey(), currentStock - qty);
        }

        stock.putAll(newStockTmp);
        log.info("Использовано деталей: ({}). Осталось на складе: ({})", describeStock(detailMap), describeStock(stock));
    }

    private static String describeStock(Map<ShipDetail, Integer> stock){
        return stock.entrySet()
                .stream().map(en -> en.getKey().name + ": " + en.getValue() + " шт.")
                .collect(Collectors.joining(", "));
    }
}
