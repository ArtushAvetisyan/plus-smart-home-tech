package ru.yandex.practicum.telemetry.analyzer.entity.scenario.actions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
public class ScenarioActionId implements Serializable {
    private Long scenario;
    private String sensor;
    private Long action;
}