package ru.yandex.practicum.telemetry.collector.kafka.producer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.kafka.topics.Topics;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducer implements AutoCloseable {
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final Topics topics;

    public void sendSensorEvent(SensorEventAvro event) {
        String key = event.getHubId().toString();
        long timestamp = event.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topics.getSensorTopic(),
                null,
                timestamp,
                key,
                event);

        producer.send(record, ((metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке события датчика в Kafka. Ключ: {}, Ошибка: {}", key, exception.getMessage(), exception);
            } else {
                log.info("Событие датчика успешно отправлено. Топик: {}, Офсет: {}", metadata.topic(), metadata.offset());
            }
        }));
    }

    public void sendHubEvent(HubEventAvro event) {
        String key = event.getHubId().toString();
        long timestamp = event.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topics.getHubTopic(),
                null,
                timestamp,
                key,
                event);

        producer.send(record, ((metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке события хаба в Kafka. Ключ: {}, Ошибка: {}", key, exception.getMessage(), exception);
            } else {
                log.info("Событие хаба успешно отправлено. Топик: {}, Офсет: {}", metadata.topic(), metadata.offset());
            }
        }));
    }

    @Override
    @PreDestroy
    public void close() {
        log.info("Закрытие Kafka Producer - а");
        producer.close();
    }
}