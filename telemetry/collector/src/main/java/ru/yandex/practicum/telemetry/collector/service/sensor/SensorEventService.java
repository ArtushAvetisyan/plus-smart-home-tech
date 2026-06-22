package ru.yandex.practicum.telemetry.collector.service.sensor;

import ru.yandex.practicum.telemetry.collector.dto.sensor.SensorEvent;

public interface SensorEventService {
    void processEvent(SensorEvent sensorEvent);
}