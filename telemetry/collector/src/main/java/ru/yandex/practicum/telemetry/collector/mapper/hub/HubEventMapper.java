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
                .setType(mapDeviceType(event.getType()))
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
                .setType(mapConditionType(condition.getType()))
                .setOperation(mapConditionOperation(condition.getOperation()))
                .setValue(conditionValue)
                .build();
    }

    private static DeviceActionAvro mapToDeviceActionAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapActionType(action.getType()))
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

    private static DeviceTypeAvro mapDeviceType(DeviceTypeProto protoType) {
        return switch (protoType) {
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип устройства: " + protoType);
        };
    }

    private static ConditionTypeAvro mapConditionType(ConditionTypeProto protoType) {
        return switch (protoType) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип условия сценария " + protoType);
        };
    }

    private static ConditionOperationAvro mapConditionOperation(ConditionOperationProto protoType) {
        return switch (protoType) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            default -> throw new IllegalArgumentException("Неподдерживаемая логическая операция условия: " + protoType);
        };
    }

    private static ActionTypeAvro mapActionType(ActionTypeProto protoType) {
        return switch (protoType) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
            default -> throw new IllegalArgumentException("Неподдерживаемый тип действия сценария: " + protoType);
        };
    }
}