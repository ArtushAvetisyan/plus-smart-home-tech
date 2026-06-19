package ru.yandex.practicum.telemetry.collector.dto.hub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioCondition {
    private String sensorId;
    private String type;
    private String operation;
    int value;
}