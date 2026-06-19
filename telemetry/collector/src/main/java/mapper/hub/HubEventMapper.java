package mapper.hub;

import dto.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

public class HubEventMapper {
    public HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        Object payload = switch (hubEvent.getType()) {
            case DEVICE_ADDED -> mapToDeviceAddedEventAvro((DeviceAddedEvent) hubEvent);
            case DEVICE_REMOVED -> mapToDeviceRemovedEventAvro((DeviceRemovedEvent) hubEvent);
            case SCENARIO_ADDED -> mapToScenarioAddedEventAvro((ScenarioAddedEvent) hubEvent);
            case SCENARIO_REMOVED -> mapToScenarioRemovedEventAvro((ScenarioRemovedEvent) hubEvent);
            default -> throw new IllegalArgumentException("Неизвестный тип события хаба: " + hubEvent.getType());
        };
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private DeviceAddedEventAvro mapToDeviceAddedEventAvro(DeviceAddedEvent event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
                .build();
    }

    private DeviceRemovedEventAvro mapToDeviceRemovedEventAvro(DeviceRemovedEvent event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    private ScenarioAddedEventAvro mapToScenarioAddedEventAvro(ScenarioAddedEvent event) {
        var conditions = event.getConditions().stream()
                .map(this::mapToScenarioConditionAvro)
                .toList();

        var actions = event.getActions().stream()
                .map(this::mapToDeviceActionAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    private ScenarioRemovedEventAvro mapToScenarioRemovedEventAvro(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    private ScenarioConditionAvro mapToScenarioConditionAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }

    private DeviceActionAvro mapToDeviceActionAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType()))
                .setValue(action.getValue())
                .build();
    }
}