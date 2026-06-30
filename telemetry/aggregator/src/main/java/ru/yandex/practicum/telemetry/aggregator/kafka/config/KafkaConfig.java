package ru.yandex.practicum.telemetry.aggregator.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {

    @Bean
    public KafkaProducer<String, SpecificRecordBase> createProducer(KafkaProperties properties) {
        log.info("Создание Kafka Producer - а");

        Map<String, Object> config = properties.buildProducerProperties(null);
        return new KafkaProducer<>(config);
    }

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createConsumer(KafkaProperties properties) {
        log.info("Создание Kafka Consumer - а");

        Map<String, Object> config = properties.buildConsumerProperties(null);
        return new KafkaConsumer<>(config);
    }
}