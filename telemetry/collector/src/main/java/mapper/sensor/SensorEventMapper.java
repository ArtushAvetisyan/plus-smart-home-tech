package mapper.sensor;

import dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

public class SensorEventMapper {

    public SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {
        Object payload = switch (sensorEvent.getType()) {
            case LIGHT_SENSOR -> mapToLightSensorAvro((LightSensorEvent) sensorEvent);
            case CLIMATE_SENSOR -> mapToClimateSensorAvro((ClimateSensorEvent) sensorEvent);
            case MOTION_SENSOR -> mapToMotionSensorAvro((MotionSensorEvent) sensorEvent);
            case SWITCH_SENSOR -> mapToSwitchSensorAvro((SwitchSensorEvent) sensorEvent);
            case TEMPERATURE_SENSOR -> mapToTemperatureSensorAvro((TemperatureSensorEvent) sensorEvent);
            default -> throw new IllegalArgumentException("Неизвестный тип события датчика: " + sensorEvent.getType());
        };
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private LightSensorAvro mapToLightSensorAvro(LightSensorEvent event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    private ClimateSensorAvro mapToClimateSensorAvro(ClimateSensorEvent event) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    private MotionSensorAvro mapToMotionSensorAvro(MotionSensorEvent event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.isMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    private SwitchSensorAvro mapToSwitchSensorAvro(SwitchSensorEvent event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.isState())
                .build();
    }

    private TemperatureSensorAvro mapToTemperatureSensorAvro(TemperatureSensorEvent event) {
        return TemperatureSensorAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }
}