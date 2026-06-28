package ru.yandex.practicum.telemetry.analyzer.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createHubEventConsumer(
            KafkaProperties properties,
            @Value("${analyzer.kafka.hub-consumer.group-id}") String groupId,
            @Value("${analyzer.kafka.hub-consumer.key-deserializer}") Class<?> keyDeserializerClass,
            @Value("${analyzer.kafka.hub-consumer.value-deserializer}") Class<?> valueDeserializerClass) {

        log.info("Создание консюмера Kafka (Hub consumer)");

        Map<String, Object> config = properties.buildConsumerProperties(null);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        return new KafkaConsumer<>(config);
    }

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> createSnapshotConsumer(
            KafkaProperties properties,
            @Value("${analyzer.kafka.snapshot-consumer.group-id}") String groupId,
            @Value("${analyzer.kafka.snapshot-consumer.key-deserializer}") Class<?> keyDeserializerClass,
            @Value("${analyzer.kafka.snapshot-consumer.value-deserializer}") Class<?> valueDeserializerClass) {

        log.info("Создание консюмера Kafka (Snapshot consumer)");

        Map<String, Object> config = properties.buildConsumerProperties(null);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        return new KafkaConsumer<>(config);
    }
}