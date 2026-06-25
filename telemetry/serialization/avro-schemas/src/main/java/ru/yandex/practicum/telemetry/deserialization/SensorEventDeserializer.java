package ru.yandex.practicum.telemetry.deserialization;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventDeserializer extends GeneralAvroDeserializer<SensorEventAvro> {
    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}