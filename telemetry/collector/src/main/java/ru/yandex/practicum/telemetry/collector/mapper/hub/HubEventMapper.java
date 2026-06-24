package ru.yandex.practicum.telemetry.collector.mapper.hub;

import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

public class HubEventMapper {
    public static HubEventAvro toHubEventAvro(HubEventProto hubEvent) {
        Instant timestamp = Instant.ofEpochSecond(
                hubEvent.getTimestamp().getSeconds(),
                hubEvent.getTimestamp().getNanos()
        );

        Object payload = switch (hubEvent.getPayloadCase()) {
            case DEVICE_ADDED -> mapToDeviceAddedEventAvro(hubEvent.getDeviceAdded());
            case DEVICE_REMOVED -> mapToDeviceRemovedEventAvro(hubEvent.getDeviceRemoved());
            case SCENARIO_ADDED -> mapToScenarioAddedEventAvro(hubEvent.getScenarioAdded());
            case SCENARIO_REMOVED -> mapToScenarioRemovedEventAvro(hubEvent.getScenarioRemoved());
            default -> throw new IllegalArgumentException("Неизвестный тип события хаба: " + hubEvent.getPayloadCase());
        };
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();
    }

    private static DeviceAddedEventAvro mapToDeviceAddedEventAvro(DeviceAddedEventProto event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeAvro.valueOf(event.getType().name()))
                .build();
    }

    private static DeviceRemovedEventAvro mapToDeviceRemovedEventAvro(DeviceRemovedEventProto event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    private static ScenarioConditionAvro mapToScenarioConditionAvro(ScenarioConditionProto condition) {
        int conditionValue = switch (condition.getValueCase()) {
            case INT_VALUE -> condition.getIntValue();
            case BOOL_VALUE -> condition.getBoolValue() ? 1 : 0;
            default -> throw new IllegalArgumentException("Значение условия сценария не задано или неизвестно");
        };

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(conditionValue)
                .build();
    }

    private static DeviceActionAvro mapToDeviceActionAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.hasValue() ? action.getValue() : null)
                .build();
    }

    private static ScenarioAddedEventAvro mapToScenarioAddedEventAvro(ScenarioAddedEventProto event) {
        var conditions = event.getConditionList().stream()
                .map(HubEventMapper::mapToScenarioConditionAvro)
                .toList();

        var actions = event.getActionList().stream()
                .map(HubEventMapper::mapToDeviceActionAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    private static ScenarioRemovedEventAvro mapToScenarioRemovedEventAvro(ScenarioRemovedEventProto event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }
}