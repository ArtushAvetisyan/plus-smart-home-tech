package ru.yandex.practicum.telemetry.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.telemetry.analyzer.kafka.processor.HubEventProcessor;
import ru.yandex.practicum.telemetry.analyzer.kafka.processor.SnapshotProcessor;

@SpringBootApplication
@ConfigurationPropertiesScan
@Slf4j
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);

        final HubEventProcessor hubEventProcessor = context.getBean(HubEventProcessor.class);
        final SnapshotProcessor snapshotProcessor = context.getBean(SnapshotProcessor.class);

        Thread hubEventsThread = new Thread(hubEventProcessor);
        hubEventsThread.setName("HubEventHandlerThread");

        log.info("Запуск потока Hub events processor");
        hubEventsThread.start();

        log.info("Запуск потока Snapshot processor");
        snapshotProcessor.run();
    }
}