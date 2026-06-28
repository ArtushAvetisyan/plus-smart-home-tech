package ru.yandex.practicum.telemetry.analyzer.kafka.processor;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.kafka.topics.Topics;
import ru.yandex.practicum.telemetry.analyzer.service.snapshot.SnapshotService;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class SnapshotProcessor implements Runnable {

    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final SnapshotService service;
    private final Topics topics;

    public SnapshotProcessor(@Qualifier("createSnapshotConsumer") KafkaConsumer<String, SpecificRecordBase> consumer,
                             Topics topics,
                             SnapshotService service) {
        this.consumer = consumer;
        this.topics = topics;
        this.service = service;
    }

    @Override
    public void run() {
        log.info("Старт Snapshot процессора");
        try {
            log.info("Snapshot консюмер подписался на топик: {}", topics.getSnapshotTopic());
            consumer.subscribe(List.of(topics.getSnapshotTopic()));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    service.processSnapshot(record);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception exception) {
            log.info("Ошибка во время обработки снэпшотов", exception);
        } finally {
            log.info("Закрытие Snapshot консюмера");
            consumer.close();
        }
    }

    @PreDestroy
    public void stop() {
        log.info("Остановка Snapshot consumer с помощью вызова wakeup");
        consumer.wakeup();
    }
}