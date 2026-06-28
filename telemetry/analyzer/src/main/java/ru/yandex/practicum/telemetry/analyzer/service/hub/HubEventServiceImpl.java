package ru.yandex.practicum.telemetry.analyzer.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.entity.Action;
import ru.yandex.practicum.telemetry.analyzer.entity.Condition;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;
import ru.yandex.practicum.telemetry.analyzer.entity.scenario.actions.ScenarioAction;
import ru.yandex.practicum.telemetry.analyzer.entity.scenario.conditions.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HubEventServiceImpl implements HubEventService {
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    @Override
    public void processHubEvent(ConsumerRecord<String, SpecificRecordBase> record) {
        if (!(record.value() instanceof HubEventAvro event)) {
            log.warn("Получена запись неподдерживаемого типа: {}", record.value() != null ? record.value().getClass().getName() : "null");
            return;
        }

        Object payload = event.getPayload();
        if (payload == null) {
            log.warn("Получено событие хаба с пустым payload: hubId - {}", event.getHubId());
            return;
        }

        String hubId = event.getHubId();

        switch (payload) {
            case DeviceAddedEventAvro deviceAdded -> handleDeviceAdded(deviceAdded, hubId);
            case DeviceRemovedEventAvro deviceRemoved -> handleDeviceRemoved(deviceRemoved, hubId);
            case ScenarioAddedEventAvro scenarioAdded -> handleScenarioAdded(scenarioAdded, hubId);
            case ScenarioRemovedEventAvro scenarioRemoved -> handleScenarioRemoved(scenarioRemoved, hubId);
            default -> {
                log.warn("Получен неизвестный тип события от хаба: {}", payload.getClass().getName());
            }
        }
    }

    private void handleDeviceAdded(DeviceAddedEventAvro deviceAdded, String hubId) {
        String sensorId = deviceAdded.getId();
        if (sensorRepository.existsById(sensorId)) {
            log.info("Датчик с id - {} уже зарегистрирован", sensorId);
            return;
        }

        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setHubId(hubId);

        sensorRepository.save(sensor);
        log.info("Датчик id - {} успешно добавлен. Хаб id - {}", sensorId, hubId);
    }

    private void handleDeviceRemoved(DeviceRemovedEventAvro deviceRemoved, String hubId) {
        String sensorId = deviceRemoved.getId();

        sensorRepository.findByIdAndHubId(sensorId, hubId).ifPresentOrElse(
                sensor -> {
                    sensorRepository.delete(sensor);
                    log.info("Датчик id - {} успешно удалён из хаба id - {}", sensorId, hubId);
                },
                () -> log.info("Датчик id - {} для хаба id - {} не найден", sensorId, hubId));
    }

    private void handleScenarioAdded(ScenarioAddedEventAvro scenarioAdded, String hubId) {
        String scenarioName = scenarioAdded.getName();

        scenarioRepository.findByHubIdAndName(hubId, scenarioName).ifPresent(
                scenario -> {
                    scenarioRepository.delete(scenario);
                    scenarioRepository.flush();
                    log.info("Старая версия сценария {} для хаба id - {} удалена перед обновлением", scenarioName, hubId);
                });

        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(scenarioName);

        if (scenarioAdded.getConditions() != null) {
            List<ScenarioCondition> conditions = scenarioAdded.getConditions().stream()
                    .map(fromAvro -> {
                        Condition condition = new Condition();
                        condition.setType(fromAvro.getType().name());
                        condition.setOperation(fromAvro.getOperation().name());
                        condition.setValue(fromAvro.getValue());

                        Sensor sensor = sensorRepository.getReferenceById(fromAvro.getSensorId());

                        ScenarioCondition sc = new ScenarioCondition();
                        sc.setScenario(scenario);
                        sc.setSensor(sensor);
                        sc.setCondition(condition);
                        return sc;
                    }).toList();
            scenario.setConditions(conditions);
        }

        if (scenarioAdded.getActions() != null) {
            List<ScenarioAction> actions = scenarioAdded.getActions().stream()
                    .map(fromAvro -> {
                        Action action = new Action();
                        action.setType(fromAvro.getType().name());
                        action.setValue(fromAvro.getValue());

                        Sensor sensor = sensorRepository.getReferenceById(fromAvro.getSensorId());

                        ScenarioAction sa = new ScenarioAction();
                        sa.setScenario(scenario);
                        sa.setSensor(sensor);
                        sa.setAction(action);
                        return sa;
                    }).toList();
            scenario.setActions(actions);
        }

        scenarioRepository.save(scenario);
        log.info("Сценарий {} для хаба id - {} успешно сохранен/обновлен", scenarioName, hubId);
    }

    private void handleScenarioRemoved(ScenarioRemovedEventAvro scenarioRemoved, String hubId) {
        String scenarioName = scenarioRemoved.getName();

        scenarioRepository.findByHubIdAndName(hubId, scenarioName).ifPresentOrElse(
                scenario -> {
                    scenarioRepository.delete(scenario);
                    log.info("Сценарий {} успешно удален из хаба id - {}", scenarioName, hubId);
                },
                () -> log.info("Сценарий {} для хаба id - {} не найден", scenarioName, hubId));
    }
}