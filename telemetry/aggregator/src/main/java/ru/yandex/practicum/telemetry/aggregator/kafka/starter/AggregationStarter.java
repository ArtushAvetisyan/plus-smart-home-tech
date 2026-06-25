package ru.yandex.practicum.telemetry.aggregator.kafka.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.kafka.service.AggregationService;
import ru.yandex.practicum.telemetry.aggregator.kafka.topics.Topics;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationStarter {
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final AggregationService aggregationService;
    private final Topics topics;

    public void start() {
        log.info("Старт Aggregator - а");
        try {
            log.info("Консюмер подписался на топик: " + topics.getSensorTopic());
            consumer.subscribe(List.of(topics.getSensorTopic()));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = (SensorEventAvro) record.value();
                    Optional<SensorsSnapshotAvro> updatedEvent = aggregationService.updateState(event);

                    if (updatedEvent.isPresent()) {
                        SensorsSnapshotAvro snapshot = updatedEvent.get();
                        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                                topics.getSnapshotTopic(), snapshot.getHubId(), snapshot);
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception exception) {
            log.error("Ошибка во время обработки событий от датчиков", exception);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрытие консьюмера");
                consumer.close();
                log.info("Закрытие продюсера");
                producer.close();
            }
        }
    }
}