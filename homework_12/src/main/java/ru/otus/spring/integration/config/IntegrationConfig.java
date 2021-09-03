package ru.otus.spring.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.spring.integration.domain.DetailsContainer;
import ru.otus.spring.integration.domain.SpaceShip;
import ru.otus.spring.integration.service.ShipFactoryService;
import ru.otus.spring.integration.service.SpacePortService;
import ru.otus.spring.integration.service.WarehouseService;

@Configuration
public class IntegrationConfig {
    private final int theftFactor;

    private static final String ASSEMBLY_FAILED_CHANNEL = "assemblyFailedChannel";
    private static final String PRODUCTION_CHANNEL = "productionChannel";
    private static final String SHIP_CHANNEL = "shipChannel";
    private static final String SUPPLY_CHANNEL = "supplyChannel";
    private static final String THEFT_CHANNEL = "theftChannel";

    public IntegrationConfig(@Value("${warehouse.theft.factor}")
                                     int theftFactor){
        this.theftFactor = theftFactor;
    }

    @Bean
    public QueueChannel productionChannel() {
        return MessageChannels.queue(10).get();
    }

    @Bean
    public PublishSubscribeChannel supplyChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel theftChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel shipChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel portChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel assemblyFailedChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedRate( 100 ).maxMessagesPerPoll( 2 ).get();
    }

    @Bean
    public IntegrationFlow supplyFlow() {
        return IntegrationFlows.from(SUPPLY_CHANNEL)
                .split()
                .publishSubscribeChannel(
                        subscription -> subscription.subscribe(
                                subflow -> subflow.<DetailsContainer>filter(f -> f.hashCode() % 10 < theftFactor)
                                        .channel(THEFT_CHANNEL)
                        ).subscribe(
                                subflow -> subflow.<DetailsContainer>filter(f -> f.hashCode() % 10 >= theftFactor)
                                        .split()
                                        .handle(WarehouseService.NAME, WarehouseService.ADD_STOCK_METHOD)
                        )
                ).aggregate().get();
    }

    @Bean
    public IntegrationFlow assemblyFlow() {
        return IntegrationFlows.from(PRODUCTION_CHANNEL)
                .split()
                .handle(ShipFactoryService.NAME, ShipFactoryService.ASSEMBLY_METHOD)
                .publishSubscribeChannel(
                        subscription -> subscription.subscribe(
                                subflow -> subflow.<SpaceShip>filter(f -> f.getUuid() == null)
                                        .channel(ASSEMBLY_FAILED_CHANNEL)
                        ).subscribe(
                                subflow -> subflow.<SpaceShip>filter(f -> f.getUuid() != null)
                                        .channel(SHIP_CHANNEL)
                        )
                ).aggregate().get();
    }

    @Bean
    public IntegrationFlow sentToPortFlow() {
        return IntegrationFlows.from(SHIP_CHANNEL)
                .split()
                .handle(SpacePortService.NAME, SpacePortService.ACCEPT_SHIP_METHOD)
                .get();
    }

    @Bean
    public IntegrationFlow theftFlow() {
        return IntegrationFlows.from(THEFT_CHANNEL)
                .split()
                .handle(WarehouseService.NAME, WarehouseService.REPORT_THEFT_METHOD)
                .get();
    }

    @Bean
    public IntegrationFlow assemblyFailedFlow() {
        return IntegrationFlows.from(ASSEMBLY_FAILED_CHANNEL)
                .split()
                .handle( ShipFactoryService.NAME, ShipFactoryService.REPORT_FAIL_METHOD)
                .get();
    }
}
