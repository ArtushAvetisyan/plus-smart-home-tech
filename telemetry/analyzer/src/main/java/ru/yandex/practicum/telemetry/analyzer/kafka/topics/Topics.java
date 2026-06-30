package ru.yandex.practicum.telemetry.analyzer.kafka.topics;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Topics {

    @Value("${app.kafka.topics.hub}")
    private String hubTopic;

    @Value("${app.kafka.topics.snapshot}")
    private String snapshotTopic;
}