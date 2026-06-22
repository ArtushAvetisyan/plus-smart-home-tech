package ru.yandex.practicum.telemetry.collector.service.hub;

import ru.yandex.practicum.telemetry.collector.dto.hub.HubEvent;

public interface HubEventService {
    void processEvent(HubEvent hubEvent);
}