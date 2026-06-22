package ru.yandex.practicum.telemetry.collector.service.hub;

import ru.yandex.practicum.telemetry.collector.dto.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.kafka.producer.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import static ru.yandex.practicum.telemetry.collector.mapper.hub.HubEventMapper.toHubEventAvro;

@Service
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {
    private final KafkaEventProducer producer;

    @Override
    public void processEvent(HubEvent hubEvent) {
        HubEventAvro avroEvent = toHubEventAvro(hubEvent);
        producer.sendHubEvent(avroEvent);
    }
}