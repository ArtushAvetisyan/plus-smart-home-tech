package ru.yandex.practicum.telemetry.collector.mapper.sensor;

import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

public class SensorEventMapper {

    public static SensorEventAvro toSensorEventAvro(SensorEventProto sensorEvent) {
        Instant timestamp = Instant.ofEpochSecond(
                sensorEvent.getTimestamp().getSeconds(),
                sensorEvent.getTimestamp().getNanos()
        );

        Object payload = switch (sensorEvent.getPayloadCase()) {
            case LIGHT_SENSOR -> mapToLightSensorAvro(sensorEvent.getLightSensor());
            case CLIMATE_SENSOR -> mapToClimateSensorAvro(sensorEvent.getClimateSensor());
            case MOTION_SENSOR -> mapToMotionSensorAvro(sensorEvent.getMotionSensor());
            case SWITCH_SENSOR -> mapToSwitchSensorAvro(sensorEvent.getSwitchSensor());
            case TEMPERATURE_SENSOR ->
                    mapToTemperatureSensorAvro(sensorEvent.getTemperatureSensor(), sensorEvent.getId(), sensorEvent.getHubId(), timestamp);
            default ->
                    throw new IllegalArgumentException("Неизвестный тип события датчика: " + sensorEvent.getPayloadCase());
        };
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();
    }

    private static LightSensorAvro mapToLightSensorAvro(LightSensorProto event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    private static ClimateSensorAvro mapToClimateSensorAvro(ClimateSensorProto event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    private static MotionSensorAvro mapToMotionSensorAvro(MotionSensorProto event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.getMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    private static SwitchSensorAvro mapToSwitchSensorAvro(SwitchSensorProto event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.getState())
                .build();
    }

    private static TemperatureSensorAvro mapToTemperatureSensorAvro(TemperatureSensorProto event,
                                                                    String id,
                                                                    String hubId,
                                                                    Instant timestamp) {
        return TemperatureSensorAvro.newBuilder()
                .setId(id)
                .setHubId(hubId)
                .setTimestamp(timestamp)
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }
}