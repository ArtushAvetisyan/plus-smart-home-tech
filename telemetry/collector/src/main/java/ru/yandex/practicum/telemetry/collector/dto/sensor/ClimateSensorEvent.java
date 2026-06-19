package ru.yandex.practicum.telemetry.collector.dto.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClimateSensorEvent extends SensorEvent {

    private int temperatureC;
    private int humidity;
    int co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR;
    }
}