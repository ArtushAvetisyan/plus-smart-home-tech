package service.hub;

import dto.hub.HubEvent;

public interface HubEventService {
    void processEvent(HubEvent hubEvent);
}