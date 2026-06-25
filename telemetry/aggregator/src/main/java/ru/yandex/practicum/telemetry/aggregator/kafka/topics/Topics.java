package ru.yandex.practicum.telemetry.aggregator.kafka.topics;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Topics {

    @Value("${app.kafka.topics.sensor}")
    private String sensorTopic;

    @Value("${app.kafka.topics.snapshot}")
    private String snapshotTopic;
}