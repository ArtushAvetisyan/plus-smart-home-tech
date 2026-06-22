package ru.yandex.practicum.telemetry.collector.dto.sensor;

import com.fasterxml.jackson.annotation.JsonAlias;

public enum SensorEventType {
    @JsonAlias("CLIMATE_SENSOR_EVENT")
    CLIMATE_SENSOR,

    @JsonAlias("LIGHT_SENSOR_EVENT")
    LIGHT_SENSOR,

    @JsonAlias("MOTION_SENSOR_EVENT")
    MOTION_SENSOR,

    @JsonAlias("SWITCH_SENSOR_EVENT")
    SWITCH_SENSOR,

    @JsonAlias("TEMPERATURE_SENSOR_EVENT")
    TEMPERATURE_SENSOR
}