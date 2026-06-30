package ru.yandex.practicum.telemetry.analyzer.service.snapshot;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface SnapshotService {
    void processSnapshot(SensorsSnapshotAvro snapshot);
}
