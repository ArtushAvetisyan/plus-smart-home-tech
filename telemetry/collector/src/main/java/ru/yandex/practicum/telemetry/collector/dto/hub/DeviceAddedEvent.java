package ru.yandex.practicum.telemetry.collector.dto.hub;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.telemetry.collector.dto.sensor.SensorEventType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceAddedEvent extends HubEvent {

    @NotBlank(message = "ID добавляемого устройства не может быть пустым")
    private String id;

    @NotNull(message = "Тип устройства не может быть пустым")
    private SensorEventType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}