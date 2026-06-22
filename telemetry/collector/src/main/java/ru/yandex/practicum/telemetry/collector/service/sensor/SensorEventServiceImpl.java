package ru.yandex.practicum.telemetry.collector.service.sensor;

import ru.yandex.practicum.telemetry.collector.dto.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.kafka.producer.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import static ru.yandex.practicum.telemetry.collector.mapper.sensor.SensorEventMapper.toSensorEventAvro;

@Service
@RequiredArgsConstructor
public class SensorEventServiceImpl implements SensorEventService {
    private final KafkaEventProducer producer;

    @Override
    public void processEvent(SensorEvent sensorEvent) {
        SensorEventAvro avroEvent = toSensorEventAvro(sensorEvent);
        producer.sendSensorEvent(avroEvent);
    }
}