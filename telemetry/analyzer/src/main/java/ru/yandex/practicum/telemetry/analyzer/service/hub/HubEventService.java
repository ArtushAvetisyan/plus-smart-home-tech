package ru.yandex.practicum.telemetry.analyzer.service.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface HubEventService {
    void processHubEvent(ConsumerRecord<String, SpecificRecordBase> record);
}