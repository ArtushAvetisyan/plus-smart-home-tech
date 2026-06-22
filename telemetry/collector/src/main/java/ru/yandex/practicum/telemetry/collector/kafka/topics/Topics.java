package ru.yandex.practicum.telemetry.collector.kafka.topics;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Topics {

    @Value("${app.Kafka.topics.sensor}")
    private String sensorTopic;

    @Value("${app.Kafka.topics.hub}")
    private String hubTopic;
}