package ru.yandex.practicum.telemetry.analyzer.service.snapshot;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SnapshotServiceImpl implements SnapshotService {
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    private final ScenarioRepository scenarioRepository;

    @Override
    public void processSnapshot(ConsumerRecord<String, SpecificRecordBase> record) {
        if (!(record.value() instanceof SensorsSnapshotAvro snapshot)) {
            log.warn("Получена запись неподдерживаемого типа: {}", record.value() != null ? record.value().getClass().getName() : "null");
            return;
        }

        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();
        if (sensorStates.isEmpty()) {
            log.warn("Получен пустой снапшот (нет активных датчиков) для хаба: {}", snapshot.getHubId());
            return;
        }

        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        scenarios.stream()
                .filter(scenario -> checkConditions(scenario, snapshot))
                .forEach(scenario -> executeActions(scenario, hubId));
    }

    private boolean checkConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        if (scenario.getConditions() == null || scenario.getConditions().isEmpty()) return false;

        return scenario.getConditions().stream()
                .allMatch(scenarioCondition -> {
                    String sensorId = scenarioCondition.getSensor().getId();
                    SensorStateAvro sensorState = snapshot.getSensorsState().get(sensorId);
                    if (sensorState == null) return false;

                    String conditionType = scenarioCondition.getCondition().getType();
                    Integer currentSensorValue = extractValue(sensorState.getData(), conditionType);
                    if (currentSensorValue == null) return false;

                    String operation = scenarioCondition.getCondition().getOperation();
                    int targetValue = scenarioCondition.getCondition().getValue();
                    return switch (operation) {
                        case "GREATER_THAN" -> currentSensorValue > targetValue;
                        case "LOWER_THAN" -> currentSensorValue < targetValue;
                        case "EQUALS" -> currentSensorValue == targetValue;
                        default -> {
                            log.warn("Неизвестная операция сравнения");
                            yield false;
                        }
                    };
                });
    }

    private void executeActions(Scenario scenario, String hubId) {
        if (scenario.getActions() == null || scenario.getActions().isEmpty()) return;

        scenario.getActions().forEach(scenarioAction -> {
            try {
                String actionType = scenarioAction.getAction().getType();
                if (actionType == null) {
                    log.warn("В сценарии '{}' отсутсвует тип действия", scenario.getName());
                    return;
                }

                ActionTypeProto actionTypeProto = switch (actionType.toUpperCase()) {
                    case "ACTIVATE" -> ActionTypeProto.ACTIVATE;
                    case "DEACTIVATE" -> ActionTypeProto.DEACTIVATE;
                    case "INVERSE" -> ActionTypeProto.INVERSE;
                    case "SET_VALUE", "SET" -> ActionTypeProto.SET_VALUE;
                    default -> {
                        log.error("Неподдерживаемый тип действия: {} для сценария {}", actionType, scenario.getName());
                        yield null;
                    }
                };
                if (actionTypeProto == null) return;

                DeviceActionProto.Builder deviceActionProto = DeviceActionProto.newBuilder()
                        .setSensorId(scenarioAction.getSensor().getId())
                        .setType(actionTypeProto);

                Integer dbValue = scenarioAction.getAction().getValue();
                if (dbValue != null) deviceActionProto.setValue(dbValue);
                DeviceActionProto finalActionProto = deviceActionProto.build();

                Instant currentInstant = Instant.now();
                long millisFromInstant = currentInstant.toEpochMilli();
                Timestamp timestamp = Timestamps.fromMillis(millisFromInstant);
                DeviceActionRequestProto deviceActionRequest = DeviceActionRequestProto.newBuilder()
                        .setHubId(hubId)
                        .setScenarioName(scenario.getName())
                        .setAction(finalActionProto)
                        .setTimestamp(timestamp)
                        .build();

                hubRouterClient.handleDeviceAction(deviceActionRequest);
                log.info("Сценарий {} успешно отправлено для устройсва с id - {}", scenario.getName(), scenarioAction.getSensor().getId());
            } catch (Exception exception) {
                log.warn("Ошибка во время отправки сценария {} для устройства с id - {}", scenario.getName(), scenarioAction.getSensor().getId(), exception);
            }
        });
    }

    private Integer extractValue(Object data, String conditionType) {
        if (data instanceof TemperatureSensorAvro temperatureSensor) {
            return temperatureSensor.getTemperatureC();

        } else if (data instanceof ClimateSensorAvro climateSensor) {
            if ("TEMPERATURE".equalsIgnoreCase(conditionType)) return climateSensor.getTemperatureC();
            if ("HUMIDITY".equalsIgnoreCase(conditionType)) return climateSensor.getHumidity();
            if ("CO2LEVEL".equalsIgnoreCase(conditionType)) return climateSensor.getCo2Level();

        } else if (data instanceof LightSensorAvro lightSensor) {
            return lightSensor.getLuminosity();

        } else if (data instanceof MotionSensorAvro motionSensor) {
            return motionSensor.getMotion() ? 1 : 0;

        } else if (data instanceof SwitchSensorAvro switchSensor) {
            return switchSensor.getState() ? 1 : 0;
        }

        return null;
    }
}