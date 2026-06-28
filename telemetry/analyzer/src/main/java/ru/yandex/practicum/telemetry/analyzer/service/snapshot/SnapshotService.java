package ru.yandex.practicum.telemetry.analyzer.service.snapshot;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface SnapshotService {
    void processSnapshot(ConsumerRecord<String, SpecificRecordBase> record);
}
