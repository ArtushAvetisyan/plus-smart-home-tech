package ru.yandex.practicum.telemetry.analyzer.kafka.processor;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.kafka.topics.Topics;
import ru.yandex.practicum.telemetry.analyzer.service.hub.HubEventService;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {

    @Value("${analyzer.kafka.hub-consumer.poll-timeout-ms}")
    private long pollTimeout;
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final HubEventService service;
    private final Topics topics;

    public HubEventProcessor(@Qualifier("createHubEventConsumer") KafkaConsumer<String, SpecificRecordBase> consumer,
                             Topics topics,
                             HubEventService service) {
        this.consumer = consumer;
        this.topics = topics;
        this.service = service;
    }

    @Override
    public void run() {
        log.info("Старт Hub процессора");
        try {
            log.info("Hub консюмер подписался на топик: {}", topics.getHubTopic());
            consumer.subscribe(List.of(topics.getHubTopic()));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(pollTimeout));
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    if (record.value() instanceof HubEventAvro event) {
                        service.processHubEvent(event);
                    } else {
                        log.warn("Получена запись неподдерживаемого типа в топике {}: {}",
                                record.topic(), record.value() != null ? record.value().getClass().getName() : "null");
                    }
                }
                if (!records.isEmpty()) consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception exception) {
            log.error("Ошибка во время обработки событий от хаба", exception);
        } finally {
            log.info("Закрытие Hub консюмера");
            consumer.commitSync();
            consumer.close();
        }
    }

    @PreDestroy
    public void stop() {
        log.info("Остановка Hub consumer с помощью вызова wakeup");
        consumer.wakeup();
    }
}