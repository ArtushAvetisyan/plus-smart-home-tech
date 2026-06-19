package ru.yandex.practicum.telemetry.collector.kafka.producer;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.kafka.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.kafka.topics.Topics;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@Slf4j
public class KafkaEventProducer implements AutoCloseable {
    private final KafkaProducer<String, SpecificRecordBase> producer = KafkaConfig.createProducer();

    public void sendSensorEvent(SensorEventAvro event) {
        String key = event.getHubId().toString();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(Topics.SENSOR_TOPIC, key, event);
        producer.send(record);
    }

    public void sendHubEvent(HubEventAvro event) {
        String key = event.getHubId().toString();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(Topics.HUB_TOPIC, key, event);
        producer.send(record);
    }


    @Override
    @PreDestroy
    public void close() {
        log.info("Закрытие Kafka Producer - а");
        producer.close();
    }
}